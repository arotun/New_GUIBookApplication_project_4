package org.book;
import java.awt.event.*;

public class Lyssnare extends WindowAdapter implements ActionListener {
    private Granssnitt grans;

    public Lyssnare(Granssnitt gransIn) {
        grans = gransIn;
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (e.getActionCommand().equals("Create a new book")) {
            grans.addBook();
        }else if (e.getActionCommand().equals("Retrieve a book by its title")) {
            grans.getBookByTitle();
        }else if (e.getActionCommand().equals("Retrieve books by category")){
            grans.getBooksByCategory();
        }else  if (e.getActionCommand().equals("Retrieve all books")) {
            grans.getAllBooks();
        }else if (e.getActionCommand().equals("Delete a book by id")) {
            grans.deleteBook();
        } else if(e.getActionCommand().equals("Retrieve a book by id")){
            grans.getBookByID();
        } else if (e.getActionCommand().equals("Exit")){
            System.exit(0);
        }
    }
}
