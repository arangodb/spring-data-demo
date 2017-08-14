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
        PRINT_STREAM.println("collection(\"book\").truncate() - cleared 'book' collection");
        PRINT_STREAM.println(BREAK);

        Book cleanCode = new Book("Clean Code");
        Book artOfProgramming = new Book("The Art of Programming");
        Book cProgrammingLanguage = new Book("The C Programming Language");

        Collection<Book> books = Arrays.asList(new Book[] {cleanCode, artOfProgramming, cProgrammingLanguage});

        PRINT_STREAM.println("All books:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(books);
        PRINT_STREAM.println();

        PRINT_STREAM.println("insertDocument(cleanCode) - inserting 'Clean Code' book");
        PRINT_STREAM.println(BREAK);
        template.insertDocument(cleanCode);
        PRINT_STREAM.println("insertDocuments([artOfProgramming, cProgrammingLanguage], Book.class) - inserting other 2 books");
        PRINT_STREAM.println(BREAK);
        template.insertDocuments(Arrays.asList(new Object[] {artOfProgramming, cProgrammingLanguage}), Book.class);
        PRINT_STREAM.println("use updateDocument(String id, Book value), if Book is already inserted");

        PRINT_STREAM.println("All 3 books after insertion:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(books);
        PRINT_STREAM.println();

        PRINT_STREAM.println("getDocument(\"" + cleanCode.getId() + "\") - expecting 'Clean Code':");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(template.getDocument(cleanCode.getId(), cleanCode.getClass()));
        PRINT_STREAM.println();

        PRINT_STREAM.println("exists(\"-\", Book.class) - expecting 'false':");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(template.exists("-", Book.class));
        PRINT_STREAM.println();

        PRINT_STREAM.println("getDocuments(Book.class) - expecting all 3 books:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(template.getDocuments(Book.class));
        PRINT_STREAM.println();

        Collection<String> ids = Arrays.asList(new String[] {artOfProgramming.getId(), cProgrammingLanguage.getId()});

        PRINT_STREAM.println("getDocuments(" + ids + ", Book.class) - expecting 'The Art of Programming' and 'The C Programming Language':");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(template.getDocuments(ids, Book.class));
        PRINT_STREAM.println();

        PRINT_STREAM.println("collection(\"book\").count() - expecting 3:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(template.collection("book").count());
        PRINT_STREAM.println();

        PRINT_STREAM.println("deleteDocument(\"" + cleanCode.getId() + "\", Book.class) - deleting 'Clean Code':");
        PRINT_STREAM.println(BREAK);
        template.deleteDocument(cleanCode.getId(), cleanCode.getClass());

        PRINT_STREAM.println("deleteDocuments([artOfProgramming.id]):");
        PRINT_STREAM.println(BREAK);
        template.deleteDocuments(Arrays.asList(new Object[] {artOfProgramming.getId()}), Book.class);

        PRINT_STREAM.println("check the database - only 'The C Programming Language' is left in 'book' collection");
    }
}
