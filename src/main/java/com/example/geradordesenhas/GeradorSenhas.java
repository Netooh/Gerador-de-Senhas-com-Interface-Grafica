package com.example.geradordesenhas;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.awt.*;
import java.net.URI;
import java.security.SecureRandom;

public class GeradorSenhas {

    @FXML private Rectangle background, background1, background2, background3;
    @FXML private Circle thumb, thumb1, thumb2, thumb3;

    private boolean toggled0 = false;
    private boolean toggled1 = false;
    private boolean toggled2 = false;
    private boolean toggled3 = false;


    private static final double TOGGLE_WIDTH = 50;
    private static final double CIRCLE_RADIUS = 11;
    private static final double CENTER = TOGGLE_WIDTH / 2;
    private static final double LEFT_POS = CIRCLE_RADIUS - CENTER;   // -14
    private static final double RIGHT_POS = TOGGLE_WIDTH - CIRCLE_RADIUS - CENTER; // +14

    @FXML private Slider slider;
    @FXML private TextField textFieldSlider;
    @FXML private ImageView imageViewLink;

    @FXML private javafx.scene.control.Button btnGerarSenha;
    @FXML private javafx.scene.control.Button btnCopiarSenha;

    @FXML private TextField campoSenha;

    private final SecureRandom aleatorio = new SecureRandom();

    private static final String LETRAS = "qwertyuioplkmnjhvgcfxdzsab";
    private static final String MAIUSCULAS = "QWERTYUIOPLKMNJHVGCFXDZSAB";
    private static final String NUMEROS = "0123456789";
    private static final String SIMBOLOS = "!@#$%¨&*()<>~^?[]+-=";


    @FXML
    public void initialize() {

        btnGerarSenha.setOnAction(e -> gerarSenha());
        btnCopiarSenha.setOnAction(e -> copiarSenha());

        imageViewLink.setOnMouseClicked((MouseEvent event) -> {
            openLink("https://github.com/Netooh");
        });

        initializeToggle(thumb, background, false);
        initializeToggle(thumb1, background1, false);
        initializeToggle(thumb2, background2, false);
        initializeToggle(thumb3, background3, false);

        // Atualiza o TextField quando o Slider mudar
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            textFieldSlider.setText(String.format("%.0f", newVal.doubleValue()));
        });

        // Atualiza o Slider quando digitar e confirmar no TextField
        textFieldSlider.setOnAction(e -> atualizarSlider());

        // Atualiza o Slider quando sair do TextField (perder foco)
        textFieldSlider.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                atualizarSlider();
            }
        });

        // Limita entrada para só números inteiros
        textFieldSlider.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                textFieldSlider.setText(newText.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void gerarSenha() {
        StringBuilder conjunto = new StringBuilder();

        if(toggled0){
            conjunto.append(MAIUSCULAS);
        }

        if(toggled1){
            conjunto.append(LETRAS);
        }

        if(toggled2){
            conjunto.append(NUMEROS);
        }

        if(toggled3){
            conjunto.append(SIMBOLOS);
        }

        if(conjunto.length() == 0){
            campoSenha.setText("Selecione ao menos uma opção!");
            return;
        }

        if(textFieldSlider.getText().isEmpty()){
            campoSenha.setText("Escolha o tamanho da Senha");
            return;
        }

        if (Integer.parseInt(textFieldSlider.getText()) < 8) {
            campoSenha.setText("Opção Invalida! Minimo 8 Digitos");
            return;
        }

        if (Integer.parseInt(textFieldSlider.getText()) > 32) {
            campoSenha.setText("Opção Invalida! Maximo 32 Digitos");
            return;
        }

        int tamanho = Integer.parseInt(textFieldSlider.getText());

        StringBuilder senha = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            int indice = aleatorio.nextInt(conjunto.length());
            senha.append(conjunto.charAt(indice));
        }

        campoSenha.setText(senha.toString());
    }

    private void copiarSenha(){
        String senha = campoSenha.getText();
        if(senha != null && !senha.isEmpty()){
            ClipboardContent conteudo = new ClipboardContent();
            conteudo.putString(senha);
            Clipboard.getSystemClipboard().setContent(conteudo);
        }
    }

    private void initializeToggle(Circle thumb, Rectangle background, boolean state) {
        thumb.setTranslateX(state ? RIGHT_POS : LEFT_POS);
        background.setFill(state ? Color.web("#4cd137") : Color.web("#ccc"));
    }

    @FXML
    private void toggle(MouseEvent event) {
        toggled0 = !toggled0;
        animateToggle(thumb, background, toggled0);
    }

    @FXML
    private void toggle1(MouseEvent event) {
        toggled1 = !toggled1;
        animateToggle(thumb1, background1, toggled1);
    }

    @FXML
    private void toggle2(MouseEvent event) {
        toggled2 = !toggled2;
        animateToggle(thumb2, background2, toggled2);
    }

    @FXML
    private void toggle3(MouseEvent event) {
        toggled3 = !toggled3;
        animateToggle(thumb3, background3, toggled3);
    }

    private void animateToggle(Circle thumb, Rectangle background, boolean state) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), thumb);
        if (state) {
            background.setFill(Color.web("#4cd137"));
            transition.setToX(RIGHT_POS);
        } else {
            background.setFill(Color.web("#ccc"));
            transition.setToX(LEFT_POS);
        }
        transition.play();
    }

    private void openLink(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.err.println("Não é possível abrir o navegador.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizarSlider() {
        try {
            double valor = Double.parseDouble(textFieldSlider.getText());
            if (valor >= slider.getMin() && valor <= slider.getMax()) {
                slider.setValue(valor);
            }
        } catch (NumberFormatException e) {
            // Pode tratar erro aqui, se quiser.
        }
        // Tira o foco do TextField
        textFieldSlider.getParent().requestFocus();
    }
}
