package org.book;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import java.util.List;
import java.util.Map;

class Granssnitt {
    private JFrame frame;
    private JPanel panel1, panel2;
    private JLabel labelTitle, labelDescription, labelYear, labelAuthor, labelCategory, labelBookId, labelCatToChoose;
    private JTextField txtTitle, txtDescription, txtYear, txtAuthor, txtCategory, txtBookId;
    private static JTextArea txtdisplay;
    private JButton buttonAllBooks, buttonSpecificBook, buttonCategory, buttonNewBook, buttonDelete, buttonExit;
    private JScrollPane scroll;
    private Lyssnare lyssna;


    public Granssnitt() {
    }

    public void Gui() {
        frame = new JFrame("Book Register");
        panel1 = new JPanel(new BorderLayout());

        labelTitle = new JLabel("Title");
        txtTitle = new JTextField(20);
        labelTitle.setBounds(50, 30, 100, 25);
        txtTitle.setBounds(130, 30, 180, 25);
        panel1.add(labelTitle);
        panel1.add(txtTitle);

        labelAuthor = new JLabel("Author");
        txtAuthor = new JTextField(20);
        labelAuthor.setBounds(50, 60, 100, 25);
        txtAuthor.setBounds(130, 60, 180, 25);
        panel1.add(labelAuthor);
        panel1.add(txtAuthor);

        labelYear = new JLabel("Year");
        txtYear = new JTextField(10);
        labelYear.setBounds(50, 90, 100, 25);
        txtYear.setBounds(130, 90, 180, 25);
        panel1.add(labelYear);
        panel1.add(txtYear);

        labelDescription = new JLabel("Description");
        txtDescription = new JTextField(10);
        labelDescription.setBounds(50, 120, 100, 25);
        txtDescription.setBounds(130, 120, 180, 25);
        panel1.add(labelDescription);
        panel1.add(txtDescription);

        labelCategory = new JLabel("Category:");
        labelCatToChoose=new JLabel("IT, Math, Physics or Economy");
        txtCategory = new JTextField(10);
        labelCategory.setBounds(50, 150, 100, 25);
        labelCatToChoose.setBounds(50, 150, 200,60);
        txtCategory.setBounds(130, 150, 180, 25);
        panel1.add(labelCategory);
        panel1.add(labelCatToChoose);
        panel1.add(txtCategory);



        labelBookId = new JLabel("Enter book ID:");
        txtBookId = new JTextField(100);
        labelBookId.setBounds(335, 190, 150, 25);
        txtBookId.setBounds(430, 190, 120, 25);
        panel1.add(labelBookId);
        panel1.add(txtBookId);

        buttonAllBooks = new JButton("Retrieve all books");
        buttonAllBooks.setBounds(325, 30, 230, 25);
        panel1.add(buttonAllBooks);

        buttonSpecificBook = new JButton("Retrieve a book by its title");
        buttonSpecificBook.setBounds(325, 60, 230, 25);
        panel1.add(buttonSpecificBook);

        buttonCategory = new JButton("Retrieve books by category");
        buttonCategory.setBounds(325, 90, 230, 25);
        panel1.add(buttonCategory);

        buttonNewBook = new JButton("Create a new book");
        buttonNewBook.setBounds(325, 120, 230, 25);
        panel1.add(buttonNewBook);

        buttonDelete = new JButton("Delete a book by id");
        buttonDelete.setBounds(325, 150, 230, 25);
        panel1.add(buttonDelete);

        buttonExit = new JButton("Exit");
        buttonExit.setBounds(350, 660, 210, 25);
        panel1.add(buttonExit);

        panel2 = new JPanel();
        lyssna = new Lyssnare(this);
        txtdisplay = new JTextArea();
        txtdisplay.setEditable(false);
        txtdisplay.setBounds(50, 250, 500, 400);
        scroll = new JScrollPane(txtdisplay);
        scroll.setBounds(50, 250, 500, 400);
        panel2.add(scroll);


        lyssna = new Lyssnare(this);
        buttonAllBooks.addActionListener(lyssna);
        buttonSpecificBook.addActionListener(lyssna);
        buttonCategory.addActionListener(lyssna);
        buttonNewBook.addActionListener(lyssna);
        buttonDelete.addActionListener(lyssna);
        buttonExit.addActionListener(lyssna);

        panel1.setLayout(new BorderLayout());
        panel2.setLayout(new BorderLayout());

        panel1.setBackground(Color.GRAY);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, (new Color(125, 125, 125))));

        panel1.add(panel2);
        frame.add(panel1);
        frame.setVisible(true);
    }

    //Method to create and add a book to the list
    public void addBook() {
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String description = txtDescription.getText();
        String category=txtCategory.getText();
        String yearText = txtYear.getText();

        if (title.isEmpty() || author.isEmpty()) {
            txtdisplay.append("Title and author cannot be empty.\n");
            return;
        }

        if (description.length() > 500) {
            txtdisplay.append("Description cannot be longer than 500 characters.\n");
            return;
        }

        if (category.isEmpty() || !isValidCategory(category)){
            txtdisplay.append("You need to state one of the following categories: IT, Math, Physics or Economy.\n");
            return;
        }

        if (!isNumeric(yearText)) {
            txtdisplay.append("Year must be a number.\n");
            return;
        }
        //Create a new Book object with the provided details
        Book newBook = new Book(txtTitle.getText(), txtDescription.getText(), txtYear.getText(), txtAuthor.getText(), txtCategory.getText());

        //Create an object mapper instance for JSON processing from the Jackson library
        //(The ObjectMapper class allows to convert Java objects to JSON and vice versa.)
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Serialize the object to JSON
            String jsonInString = objectMapper.writeValueAsString(newBook);

            String postUrl = "http://localhost:8080/books";

            // Create an HttpClient instance
            HttpClient httpClient = HttpClients.createDefault();

            // Create an HttpPost with the URL
            HttpPost httpPost = new HttpPost(postUrl);

            // Set the Content-Type header
            httpPost.setHeader("Content-Type", "application/json");

            // Set the JSON payload
            StringEntity entity = new StringEntity(jsonInString);
            httpPost.setEntity(entity);

            // Execute the request and get the response
            HttpResponse response = httpClient.execute(httpPost);

            // Retrieve the status code from the HTTP response and store it in the responseCode variable.
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode == HttpURLConnection.HTTP_CREATED) {//HTTP Status-Code 201: Created
                txtdisplay.append("Book created successfully.\n");

                // Read the JSON content from the HTTP response,deserialize it into an instance of the Book class using the ObjectMapper,
                // and assigns the Book object to the createdBook variable.
                Book createdBook = objectMapper.readValue(response.getEntity().getContent(), Book.class);
                txtdisplay.append("ID: " + createdBook.getId() + ", Title: " + createdBook.getTitle() + ", Author: " + createdBook.getAuthor() + ", Year: " + createdBook.getPublished_year() + ", Description: " + createdBook.getDescription() + ", Category: " + createdBook.getCategory() + "\n");


            } else {
                txtdisplay.append("Failed to create book. Response Code: " + responseCode + "\n");
            }
            getAllBooks(); // Refresh the book list after adding a new book

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Clear the text fields
        txtTitle.setText("");
        txtAuthor.setText("");
        txtYear.setText("");
        txtDescription.setText("");
        txtCategory.setText("");

    }

}
    //Method to check if the category is valid
    private boolean isValidCategory(String category) {
        try {
            // Check if the category matches any of the enum values in Book.Category
            Book.Category.valueOf(category);
            return true;// If yes, return true
        } catch (IllegalArgumentException e) {
            return false;// If no match is found, return false
        }
    }

    // Method to check if a string is numeric
    public boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public void newMessage(String s) {
        txtdisplay.append(s+"\n");
    }

    //Method to retrieve and display all books
    public void getAllBooks() {
        txtdisplay.append("====== Book List ========\n");
        //Create an object mapper instance for JSON processing
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // URL of the endpoint to fetch all books
            URL url = new URL("http://localhost:8080/books");

           // Open a connection to the URL and set the request method to GET
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code from the server
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {//HTTP Status-Code 204: No Content.
                txtdisplay.append("The list is empty.\n");
                txtdisplay.append("------------------------\n");

            } else if (responseCode == HttpURLConnection.HTTP_OK) {//HTTP Status-Code 200: OK.

                // Deserialize the JSON response into a List of Maps
                List<Map<String, Object>> bookList = objectMapper.readValue(url, new TypeReference<List<Map<String, Object>>>() {});

                // Iterate through the list and retrieve book details
                for (Map<String, Object> bookMap : bookList) {
                    Long id = ((Number) bookMap.get("id")).longValue();
                    String title = (String) bookMap.get("title");
                    String published_year = (String) bookMap.get("published_year");
                    String author = (String) bookMap.get("author");
                    String description = (String) bookMap.get("description");
                    String category = (String) bookMap.get("category");

                    // Append the book details to the text display
                    txtdisplay.append("ID: " + id + ", Title: " + title + ", Author: " + author + ", Year: " + published_year + ", Description: " + description + ", Category:" + category + "\n");

                }
                txtdisplay.append("------------------------\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve and display books by title
    public void getBookByTitle() {
        //Create an object mapper instance for JSON processing
        ObjectMapper objectMapper = new ObjectMapper();

        // Retrieve the book title from the input field
        String title = txtTitle.getText();

        try {
            // URL of the endpoint to fetch books by title
            URL url = new URL("http://localhost:8080/books/title/" + title);

            // Open a connection to the URL and set the request method to GET
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code from the server
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {//HTTP Status-Code 200: OK

                // Deserialize the JSON response into a List of Maps
                List<Map<String, Object>> bookList = objectMapper.readValue(url, new TypeReference<List<Map<String, Object>>>() {
                });

                // Check if the book list is not empty and iterate through the list and retrieve book details
                if (!bookList.isEmpty()) {
                for (Map<String, Object> bookMap : bookList) {
                    Long id = ((Number) bookMap.get("id")).longValue();
                    String bookTitle = (String) bookMap.get("title");
                    String publishedYear = (String) bookMap.get("published_year");
                    String author = (String) bookMap.get("author");
                    String description = (String) bookMap.get("description");
                    String category = (String) bookMap.get("category");

                    //Append the book details to the text display
                    txtdisplay.append("ID: " + id + ", Title: " + bookTitle + ", Published Year: " + publishedYear + ", Author: " + author + ", Description: " + description + ", Category: " + category + "\n");

                }
                txtdisplay.append("------------------------\n");
                }
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {// HTTP Status-Code 404: Not Found
                txtdisplay.append("No title provided or no book(s) found with the title:" + title + "\n");
                txtdisplay.append("------------------------\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            txtdisplay.append("No title provided or no connection to URL: " + e.getMessage() + "\n");
            txtdisplay.append("------------------------\n");
        } finally {
            txtTitle.setText("");// Clear the text field
        }
    }


// Method to retrieve and display books by category
    public void getBooksByCategory() {
        // Create an object mapper instance for JSON processing
        ObjectMapper objectMapper = new ObjectMapper();
        // Retrieve the book category from the input field
        String category = txtCategory.getText();


        try {
            // URL of the endpoint to fetch books by category
            URL url = new URL("http://localhost:8080/books/category/" + category);

            // Open a connection to the URL and set the request method to GET
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code from the server
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // HTTP Status-Code 200: OK

                // Deserialize the JSON response into a List of Maps
                List<Map<String, Object>> bookList = objectMapper.readValue(connection.getInputStream(), new TypeReference<List<Map<String, Object>>>() {});

                // Check if the book list is not empty and iterate through the list and retrieve book details
                if (!bookList.isEmpty()) {
                    for (Map<String, Object> bookMap : bookList) {
                        Long id = ((Number) bookMap.get("id")).longValue();
                        String bookTitle = (String) bookMap.get("title");
                        String publishedYear = (String) bookMap.get("published_year");
                        String author = (String) bookMap.get("author");
                        String description = (String) bookMap.get("description");
                        category = (String) bookMap.get("category");

                        // Append the book details to the text display
                        txtdisplay.append("ID: " + id + ", Title: " + bookTitle + ", Published Year: " + publishedYear + ", Author: " + author + ", Description: " + description + ", Category: " + category + "\n");
                    }
                    txtdisplay.append("------------------------\n");
                } else {
                    txtdisplay.append("No book found with the category: " + category + "\n");
                    txtdisplay.append("------------------------\n");
                }
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) { // HTTP Status-Code 404: Not Found
                txtdisplay.append("No or wrong category provided\n");
                txtdisplay.append("------------------------\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            txtdisplay.append("Error connecting to URL: " + e.getMessage() + "\n");
            txtdisplay.append("------------------------\n");
        } finally {
            txtCategory.setText(""); // Clear the text field
        }
    }

    // Method to delete a book by ID
    public void deleteBook() {
        //Create an object mapper instance for JSON processing
        ObjectMapper objectMapper = new ObjectMapper();

        // Retrieve the book ID from the input field
        String bookID= txtBookId.getText();

        // Check if the book ID is empty
        if (bookID.isEmpty()){
            txtdisplay.append("No book ID provided. Please enter a book ID.\n");
            txtdisplay.append("------------------------\n");
            return;
        }

        //Convert the book ID to a Long
        Long id = Long.parseLong(bookID);

        try {
            // URL of the endpoint to delete books by ID
            URL url = new URL("http://localhost:8080/books/" + id);

            // Open a connection to the URL and set the request method to DELETE
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            // Get the response code from the server
            int responseCode = connection.getResponseCode();

            // Check the response code and append an appropriate message to the display
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {//HTTP Status-Code 204: No Content.
                txtdisplay.append("Book deleted successfully.\n");

            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {//HTTP Status-Code 404: Not Found.
                txtdisplay.append("Book not found.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            txtdisplay.append("No connection to URL: " + e.getMessage() + "\n");
        } finally {
            txtBookId.setText(""); // Clear the text field
        }
    }

}