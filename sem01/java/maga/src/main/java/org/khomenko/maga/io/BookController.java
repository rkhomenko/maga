package org.khomenko.maga.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.khomenko.maga.http.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Route(path = "/books")
public class BookController {
    private static final Logger logger = LogManager.getLogger(BookController.class);

    private String route;
    private SerializationHelper<Book> serializationHelper;

    public BookController() {
        Route route = this.getClass().getAnnotation(Route.class);
        this.route = route.path();
        this.serializationHelper = new SerializationHelper<>(Book.class);
    }

    @RouteHandler(type = HttpMethod.GET)
    public HttpResponse getAllBooks(String requestBody) {
        StringBuilder stringBuilder = new StringBuilder();

        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(HttpStatusCode.OK);

        Path booksDir = Path.of("." + route);
        if (!Files.exists(booksDir)) {
            return httpResponse;
        }

        try (Stream<Path> files = Files.walk(booksDir)) {
            files.skip(1).forEach(path -> {
                stringBuilder.append(serializationHelper.readFile(path.toString()))
                        .append("\n\r");
            });
        }
        catch (IOException e) {
            logger.fatal(e.getMessage());
            return null;
        }

        httpResponse.setContentType(HttpResponse.ContentType.HTML);
        httpResponse.setBody(stringBuilder.toString());

        return httpResponse;
    }

    @RouteHandler(type = HttpMethod.GET, withArgument = true)
    public HttpResponse getBook(String requestBody, String isbn) {
        HttpResponse response = new HttpResponse();

        Path bookPath = Path.of(getBookPath(isbn));
        if (!Files.exists(bookPath)) {
            response.setStatusCode(HttpStatusCode.NOT_FOUND);
        }
        else {
            String body = serializationHelper.readFile(getBookPath(isbn));
            if (body == null) {
                response.setStatusCode(HttpStatusCode.SERVER_ERROR);
            } else {
                response.setStatusCode(HttpStatusCode.OK);
                response.setContentType(HttpResponse.ContentType.JSON);
                response.setBody(body);
            }
        }

        return response;
    }

    @RouteHandler(type = HttpMethod.POST)
    public HttpResponse createBook(String requestBody) {
        Book book = serializationHelper.fromString(requestBody);
        if (book == null) {
            return null;
        }

        if (!serializationHelper.saveToFile(getBookPath(book), book)) {
            return null;
        }

        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(HttpStatusCode.OK);
        httpResponse.setContentType(HttpResponse.ContentType.JSON);
        httpResponse.setBody("{\"status\": \"ok\"}");

        return httpResponse;
    }

    @RouteHandler(type = HttpMethod.DELETE, withArgument = true)
    public HttpResponse deleteBook (String requestBody, String isbn) {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);

        Path bookPath = Path.of(getBookPath(isbn));
        if (Files.exists(bookPath)) {
            try {
                Files.delete(bookPath);
            }
            catch (IOException e) {
                return null;
            }
        }

        return response;
    }

    private String getBookPath(String isbn) {
        return "." + route + "/" + isbn + ".json";
    }

    private String getBookPath(Book book) {
        return getBookPath(book.getIsbn());
    }
}
