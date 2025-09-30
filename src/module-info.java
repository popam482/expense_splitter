module expense_splitter {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.base;
	requires pdfbox.app;
	
	opens application to javafx.graphics, javafx.fxml;
}
