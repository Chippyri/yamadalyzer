module chippyri.yamadalyzer {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
    opens chippyri.yamadalyzer to javafx.fxml;
    exports chippyri.yamadalyzer;
}