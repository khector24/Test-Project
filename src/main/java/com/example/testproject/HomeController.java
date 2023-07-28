package com.example.testproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    public TextField questionTfld;
    public TextArea answerTextArea;
    public Button addBtn;
    public Button updateBtn;
    public Button deleteBtn;
    public Button clearBtn;
    public TableColumn<FAQ, Integer> idCol;
    public TableColumn<FAQ, String> questionCol;
    public TableColumn<FAQ, String> answerCol;
    public TableView<FAQ> faqTable;
    @FXML
    public Label notificationLbl;
    public Button refreshBtn;
    public TextField idTfld;
    public Button selectBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpTableColumns();
        loadDataIntoFAQTable();
    }

    public void setUpTableColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        questionCol.setCellValueFactory(new PropertyValueFactory<>("question"));
        questionCol.setCellFactory(tc -> {
            TableCell<FAQ, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(questionCol.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        answerCol.setCellValueFactory(new PropertyValueFactory<>("answer"));
        answerCol.setCellFactory(tc -> {
            TableCell<FAQ, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(answerCol.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }

    public void loadDataIntoFAQTable() {
        ObservableList<FAQ> faqs = FXCollections.observableArrayList();
        Connection connection = new DatabaseConnection().getConnection();

        String loadQuery = "SELECT * FROM faq_table";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(loadQuery)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String question = resultSet.getString("question");
                String answer = resultSet.getString("answer");

                FAQ faq = new FAQ(id, question, answer);
                faqs.add(faq);
            }

            faqTable.setItems(faqs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addBtnOnAction(ActionEvent event) {
        // Get the question and answer from the input fields
        String question = questionTfld.getText();
        String answer = answerTextArea.getText();

        // Check if the input fields are empty
        if (question.isEmpty() || answer.isEmpty()) {
            showAlert("Error", "Please enter both question and answer.");
            return;
        }

        try (Connection connection = new DatabaseConnection().getConnection();) {
            // Prepare the insert query
            String query = "INSERT INTO faq_table (question, answer) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, question);
            preparedStatement.setString(2, answer);
            preparedStatement.executeUpdate();
            showAlert("Success", "FAQ added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add FAQ to the database.");
        }

        clear(); // Clear the input fields after adding the FAQ
        loadDataIntoFAQTable(); // Refresh the tableview
    }

    @FXML
    public void updateBtnOnAction(ActionEvent event) {
        notificationLbl.setText("");
        FAQ selectedFAQ = faqTable.getSelectionModel().getSelectedItem();

        if (selectedFAQ != null) {
            if (showConfirmationAlert("Inquiry", "Would you like to edit the question with ID: " + selectedFAQ.getId())) {
                String newQuestion = questionTfld.getText();
                String newAnswer = answerTextArea.getText();

                // Check if the input fields are empty
                if (newQuestion.isEmpty() || newAnswer.isEmpty()) {
                    showAlert("Error", "Please enter both question and answer.");
                    return;
                }

                try (Connection connection = new DatabaseConnection().getConnection();) {
                    // Prepare the update query
                    String updateQuery = "UPDATE faq_table SET question=?, answer=? WHERE id=?";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, newQuestion);
                    preparedStatement.setString(2, newAnswer);
                    preparedStatement.setInt(3, selectedFAQ.getId());
                    preparedStatement.executeUpdate();
                    showAlert("Success", "FAQ updated successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to update FAQ in the database.");
                }

                clear(); // Clear the input fields after updating the FAQ
                loadDataIntoFAQTable(); // Reload the data into the table
            }
        } else {
            showAlert("Error", "Please select a row to be edited.");
        }
    }


    @FXML
    public void deleteBtnOnAction(ActionEvent event) {
        FAQ selectedFAQ = faqTable.getSelectionModel().getSelectedItem();

        if (selectedFAQ != null) {
            if (showConfirmationAlert("Inquiry", "Would you like to edit the question with ID: " + selectedFAQ.getId())) {
                Connection connection = new DatabaseConnection().getConnection();

                String deleteQuery = "DELETE FROM faq_table WHERE id=?";

                try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                    statement.setInt(1, selectedFAQ.getId());
                    statement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                loadDataIntoFAQTable();
                notificationLbl.setText("Item deleted successfully!");
                notificationLbl.setStyle("-fx-text-fill: green;");
            }
        }
    }

    public void clearBtnOnAction(ActionEvent event) {
        clear();
    }


    @FXML
    public void refreshBtnOnAction(ActionEvent event) {
        loadDataIntoFAQTable();

        notificationLbl.setText("The page was refreshed successfully!");
        notificationLbl.setStyle("-fx-text-fill: green;");
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Set custom buttons for the alert
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show the alert and wait for user response
        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == yesButton;
    }

    public void clear() {
        questionTfld.clear();
        answerTextArea.clear();
    }

    public void selectBtnOnAction(ActionEvent event) {
        FAQ selectedFAQ = faqTable.getSelectionModel().getSelectedItem();

        if (selectedFAQ != null) {
            questionTfld.setText(selectedFAQ.getQuestion());
            answerTextArea.setText(selectedFAQ.getAnswer());
        }
    }
}

