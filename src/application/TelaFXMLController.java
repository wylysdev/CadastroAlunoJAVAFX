package application;

import java.net.URL;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class TelaFXMLController implements Initializable {

    // Componentes visuais
    // Labels para exibir os valores de id e nome
    @FXML
    private Label id;
    @FXML
    private Label nome;

    // TextFields para entrada de dados
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNome;

    // TableView para exibir os dados de alunos
    @FXML
    private TableView<Aluno> tabelaAlunos;

    // TableColumn para exibir os ids dos alunos
    @FXML
    private TableColumn<Aluno, Integer> colunaId;

    // TableColumn para exibir os nomes dos alunos
    @FXML
    private TableColumn<Aluno, String> nomeCol;

    // Lista observável de alunos
    ObservableList<Aluno> list = FXCollections.observableArrayList();

    // Botões para realizar ações na tabela
    @FXML
    private Button btnInsert;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Configura as colunas da tabela
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        iniciarTable(); // Preenche a tabela com os dados do banco de dados
        tabelaAlunos.setItems(list); // Associa a lista de alunos à tabela

        txtId.setText("");
        txtNome.setText("");

        // btn insert - id e text sejam diferentes de vazio
        // btn delete e btn update - id seja diferentes de vazio
        // Registra um listener para o campo txtId
        // Desativa os botões de ação quando os campos de texto estiverem vazios
        txtId.textProperty().addListener((observable, oldValue, newValue) -> {
            verificarCamposInsert();
            verificarCamposUpdateDelete();
        });

        // Registra um listener para o campo txtNome
        txtNome.textProperty().addListener((observable, oldValue, newValue) -> {
            verificarCamposInsert();
        });

    }

    // Ações do usuário
    public void actionTexto(ActionEvent event) {
        id.setText(txtId.getText());
        nome.setText(txtNome.getText());
    }

    @FXML
    public void actionSQLInsert(ActionEvent event) {
        try {
            DBUtil db = DBUtil.getInstance();
            PreparedStatement ps = db.getConnection().prepareStatement("Insert into aluno (id, nome) values (?, ?)");
            ps.setInt(1, Integer.parseInt(txtId.getText()));
            ps.setString(2, txtNome.getText());
            ps.execute();

            // Adiciona os dados inseridos na tabela
            ObservableList<Aluno> data = tabelaAlunos.getItems();
            data.add(new Aluno(Integer.parseInt(txtId.getText()), txtNome.getText()));

            // inseriu? zera os campos de textfild e desativa o botão
            txtId.setText("");
            txtNome.setText("");
            btnInsert.setDisable(true);

        } catch (Exception e) {

            //Para exibir um alerta em JavaFX, você pode usar a classe Alert do pacote  Por exemplo:
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ATENÇÃO");
            alert.setHeaderText("Ocorrou um erro ao inserir");
            alert.setContentText("Erro: " + e.toString());
            alert.showAndWait();
            System.out.println("Erro: " + e.toString());
        }

        iniciarTable();
    }

    @FXML
    public void actionSQLSelect(ActionEvent event) {

        txtId.setText("");
        txtNome.setText("");
        id.setText("");
        nome.setText("");
        iniciarTable();
    }

    @FXML
    public void actionSQLDelete(ActionEvent event) {
        try {
            DBUtil db = DBUtil.getInstance();
            PreparedStatement ps = db.getConnection().prepareStatement("Delete from aluno where id = ?");
            ps.setInt(1, Integer.parseInt(txtId.getText()));
            ps.execute();
        } catch (Exception e) {
            //Para exibir um alerta em JavaFX, você pode usar a classe Alert do pacote  Por exemplo:
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ATENÇÃO");
            alert.setHeaderText("Ocorrou um erro ao Deletar");
            alert.setContentText("Erro: " + e.toString());
            alert.showAndWait();
            System.out.println("Erro: " + e.toString());
        }
        iniciarTable();
    }

    @FXML
    public void actionSQLUpdate(ActionEvent event) {
        try {
            DBUtil db = DBUtil.getInstance();
            PreparedStatement ps = db.getConnection().prepareStatement("update aluno set nome = ? where id = ?");
            ps.setString(1, txtNome.getText());
            ps.setInt(2, Integer.parseInt(txtId.getText()));
            ps.execute();
        } catch (Exception e) {
            //Para exibir um alerta em JavaFX, você pode usar a classe Alert do pacote  Por exemplo:
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ATENÇÃO");
            alert.setHeaderText("Ocorrou um erro ao Atualizar");
            alert.setContentText("Erro: " + e.toString());
            alert.showAndWait();
            System.out.println("Erro: " + e.toString());
        }
        iniciarTable();
    }

    public void iniciarTable() {
        list.clear();
        try {
            DBUtil db = DBUtil.getInstance();
            PreparedStatement ps;
            ps = db.getConnection().prepareStatement("SELECT * from aluno");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(
                        new Aluno(rs.getInt("id"), rs.getString("nome")
                        ));
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.toString());
        }
    }

    @FXML
    private void onMouseClick(MouseEvent event) {
        Aluno aluno = tabelaAlunos.getSelectionModel().getSelectedItem();
        txtId.setText(aluno.getId().toString());
        txtNome.setText(aluno.getNome());
        System.out.println("id " + aluno.getId());
        System.out.println("nome " + aluno.getNome());
    }

    // esse método verifica se os campos de entrada 
    // de dados estão vazios. Primeiro, pega-se o valor do campo de ID e 
    // converte para inteiro. Depois verifica-se se o campo ID já 
    // existe usando um método chamado verificarID (idLocal). Se o campo 
    // ID não existir, então verifica-se se os campos de entrada de dados 
    // (ID e Nome) estão vazios. Se os campos não estiverem vazios, então o 
    // botão Insert é ativado. Se o campo ID já existir, o botão Insert é desativado.
    private void verificarCamposInsert() {

        int idLocal = Integer.parseInt(txtId.getText());

        if (!verificarID(idLocal)) {
            //!txtId.getText().trim().isEmpty() && 
            boolean camposPreenchidos = !txtNome.getText().trim().isEmpty();
            btnInsert.setDisable(!camposPreenchidos);
        } else {
            btnInsert.setDisable(true);
        }

    }

    //esse método é responsável por verificar se os campos para update e delete estão preenchidos. 
    //Se sim, os respectivos botões serão ativados, caso contrário, ficarão desabilitados.
    //Além disso, também é verificado se o ID informado é válido.
    private void verificarCamposUpdateDelete() {

        int idLocal = Integer.parseInt(txtId.getText());

        if (verificarID(idLocal)) {
            boolean idPreenchido = !txtId.getText().trim().isEmpty();
            btnUpdate.setDisable(!idPreenchido);
            btnDelete.setDisable(!idPreenchido);
        } else {
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        }

    }

    public boolean verificarID(int id) {
        try {
            DBUtil db = DBUtil.getInstance();
            PreparedStatement ps = db.getConnection().prepareStatement("SELECT * from aluno WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Erro: " + e.toString());
            return false;
        }
    }

}
