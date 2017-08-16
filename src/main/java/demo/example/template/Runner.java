package demo.example.template;

import com.arangodb.springframework.core.ArangoOperations;
import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.Collection;

@ComponentScan("demo")
public class Runner extends AbstractRunner {

    @Autowired
    ArangoOperations template;

    @Override
    public void run(String... args) throws Exception {
        template.collection("book").truncate();
        LOGGER.log("collection(\"book\").truncate() - cleared 'book' collection");
        LOGGER.log(BREAK);

        Book cleanCode = new Book("Clean Code");
        Book artOfProgramming = new Book("The Art of Programming");
        Book cProgrammingLanguage = new Book("The C Programming Language");

        Collection<Book> books = Arrays.asList(new Book[] {cleanCode, artOfProgramming, cProgrammingLanguage});

        LOGGER.log("All books:");
        LOGGER.log(BREAK);
        LOGGER.log(books);
        LOGGER.log();

        LOGGER.log("insertDocument(cleanCode) - inserting 'Clean Code' book");
        LOGGER.log(BREAK);
        template.insertDocument(cleanCode);
        LOGGER.log("insertDocuments([artOfProgramming, cProgrammingLanguage], Book.class) - inserting other 2 books");
        LOGGER.log(BREAK);
        template.insertDocuments(Arrays.asList(new Object[] {artOfProgramming, cProgrammingLanguage}), Book.class);
        LOGGER.log("use updateDocument(String id, Book value), if Book is already inserted");

        LOGGER.log("All 3 books after insertion:");
        LOGGER.log(BREAK);
        LOGGER.log(books);
        LOGGER.log();

        LOGGER.log("getDocument(\"" + cleanCode.getId() + "\") - expecting 'Clean Code':");
        LOGGER.log(BREAK);
        LOGGER.log(template.getDocument(cleanCode.getId(), cleanCode.getClass()));
        LOGGER.log();

        LOGGER.log("exists(\"-\", Book.class) - expecting 'false':");
        LOGGER.log(BREAK);
        LOGGER.log(template.exists("-", Book.class));
        LOGGER.log();

        LOGGER.log("getDocuments(Book.class) - expecting all 3 books:");
        LOGGER.log(BREAK);
        LOGGER.log(template.getDocuments(Book.class));
        LOGGER.log();

        Collection<String> ids = Arrays.asList(new String[] {artOfProgramming.getId(), cProgrammingLanguage.getId()});

        LOGGER.log("getDocuments(" + ids + ", Book.class) - expecting 'The Art of Programming' and 'The C Programming Language':");
        LOGGER.log(BREAK);
        LOGGER.log(template.getDocuments(ids, Book.class));
        LOGGER.log();

        LOGGER.log("collection(\"book\").count() - expecting 3:");
        LOGGER.log(BREAK);
        LOGGER.log(template.collection("book").count());
        LOGGER.log();

        LOGGER.log("deleteDocument(\"" + cleanCode.getId() + "\", Book.class) - deleting 'Clean Code':");
        LOGGER.log(BREAK);
        template.deleteDocument(cleanCode.getId(), cleanCode.getClass());

        LOGGER.log("deleteDocuments([artOfProgramming.id]):");
        LOGGER.log(BREAK);
        template.deleteDocuments(Arrays.asList(new Object[] {artOfProgramming.getId()}), Book.class);

        LOGGER.log("check the database - only 'The C Programming Language' is left in 'book' collection");
    }
}
