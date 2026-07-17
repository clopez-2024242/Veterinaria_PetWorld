package org.carloslopez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.carloslopez.bean.Vacuna;
import org.carloslopez.db.Conexion;
import org.carloslopez.system.Principal;

public class VacunaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR,ELIMINAR,ACTUALIZAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Vacuna> listaVacuna;
    
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnNuevo;
    @FXML private Button btnReporte;
    @FXML private TextField txtCodigoVacuna;
    @FXML private TextField txtNombreVacuna;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtDosis;
    @FXML private TextField txtFrecuencia;
    @FXML private TableView tblVacunas;
    @FXML private TableColumn colCodigoVacuna;
    @FXML private TableColumn colNombreVacuna;
    @FXML private TableColumn colDescripcion;
    @FXML private TableColumn colDosis;
    @FXML private TableColumn colFrecuencia;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private ImageView imgEliminar;
    
    public Principal getEscenarioPrincipal(){
        return escenarioPrincipal;
    }
    public void setEscenarioPrincipal(Principal escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UnaryOperator<TextFormatter.Change> soloNumeros = bloqueo -> {
            String texto = bloqueo.getControlNewText();
            if (texto.matches("\\d*")) 
                return bloqueo;
            return null;
        };
        txtFrecuencia.setTextFormatter(new TextFormatter<>(soloNumeros));
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblVacunas.setItems(getVacuna());
        colCodigoVacuna.setCellValueFactory(new PropertyValueFactory<Vacuna,Integer>("codigoVacuna"));
        colNombreVacuna.setCellValueFactory(new PropertyValueFactory<Vacuna,String>("nombreVacuna"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Vacuna,String>("descripcion"));
        colDosis.setCellValueFactory(new PropertyValueFactory<Vacuna,String>("dosis"));
        colFrecuencia.setCellValueFactory(new PropertyValueFactory<Vacuna,String>("frecuenciaMeses"));
    }
    
    public void seleccionarElemento(){
        if (tblVacunas.getSelectionModel().getSelectedItem() != null) {
            txtCodigoVacuna.setText(String.valueOf((((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getCodigoVacuna())));
            txtNombreVacuna.setText(((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getNombreVacuna());
            txtDescripcion.setText(((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getDescripcion());
            txtDosis.setText(((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getDosis());
            txtFrecuencia.setText(String.valueOf(((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getFrecuenciaMeses()));
        }else{
            JOptionPane.showMessageDialog(null, "No hay nada Seleccionado");
        }
    }
    
    public ObservableList<Vacuna> getVacuna(){
        ArrayList<Vacuna> lista = new ArrayList<>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarVacunas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Vacuna(
                    resultado.getInt("codigoVacuna"),
                    resultado.getString("nombreVacuna"),
                    resultado.getString("descripcion"),
                    resultado.getString("dosis"),
                    resultado.getInt("frecuenciaMeses")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaVacuna = FXCollections.observableArrayList(lista);
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                imgNuevo.setImage(new Image("/org/carloslopez/image/PerroBotonGuardar.png"));
                imgEliminar.setImage(new Image("/org/carloslopez/image/PerroBotonCancelar.png"));
                tipoDeOperacion = operaciones.GUARDAR;
            break;
            case GUARDAR:
                if(controlesVacios()){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    imgNuevo.setImage(new Image("/org/carloslopez/image/PerroBotonAdd.png"));
                    imgEliminar.setImage(new Image("/org/carloslopez/image/PerroBotonRemove.png"));
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe rellenar todos los datos");
                }
            break;
        }
    }
    
    public void guardar(){
        Vacuna registro = new Vacuna();
        registro.setNombreVacuna(txtNombreVacuna.getText());
        registro.setDescripcion(txtDescripcion.getText());
        registro.setDosis(txtDosis.getText());
        registro.setFrecuenciaMeses(Integer.parseInt(txtFrecuencia.getText()));
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarVacuna(?,?,?,?)}");
            procedimiento.setString(1, registro.getNombreVacuna());
            procedimiento.setString(2, registro.getDescripcion());
            procedimiento.setString(3, registro.getDosis());
            procedimiento.setInt(4, registro.getFrecuenciaMeses());
            procedimiento.execute();
            listaVacuna.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/carloslopez/image/PerroBotonAdd.png"));
                imgEliminar.setImage(new Image("/org/carloslopez/image/PerroBotonRemove.png"));
                tipoDeOperacion= operaciones.NINGUNO;
            break;
            default:
                if(tblVacunas.getSelectionModel().getSelectedItem() !=null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Vacuna",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarVacuna(?)}");
                            procedimiento.setInt(1, ((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getCodigoVacuna());
                            procedimiento.execute();
                            listaVacuna.remove(tblVacunas.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
            break;
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblVacunas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    imgEditar.setImage(new Image("/org/carloslopez/image/PerroBotonGuardar.png"));
                    imgReporte.setImage(new Image("/org/carloslopez/image/PerroBotonCancelar.png"));
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else
                    JOptionPane.showMessageDialog(null, "Debe Seleccionar un elemento");
            break;
            case ACTUALIZAR:
                actualizar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("/org/carloslopez/image/PerroBotonEdit.png"));
                imgReporte.setImage(new Image("/org/carloslopez/image/PerroBotonReport.png"));
                cargarDatos();
                tipoDeOperacion = operaciones.NINGUNO;
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarVacuna(?,?,?,?,?)}");
            Vacuna registro = (Vacuna)tblVacunas.getSelectionModel().getSelectedItem();
            registro.setNombreVacuna(txtNombreVacuna.getText());
            registro.setDescripcion(txtDescripcion.getText());
            registro.setDosis(txtDosis.getText());
            registro.setFrecuenciaMeses(Integer.parseInt(txtFrecuencia.getText()));
            procedimiento.setInt(1, registro.getCodigoVacuna());
            procedimiento.setString(2, registro.getNombreVacuna());
            procedimiento.setString(3, registro.getDescripcion());
            procedimiento.setString(4, registro.getDosis());
            procedimiento.setInt(5, registro.getFrecuenciaMeses());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void generarReporte(){
        switch(tipoDeOperacion){
            case NINGUNO:
                
            break;
            case ACTUALIZAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("/org/carloslopez/image/PerroBotonEdit.png"));
                imgReporte.setImage(new Image("/org/carloslopez/image/PerroBotonReport.png"));
                cargarDatos();
                tipoDeOperacion = operaciones.NINGUNO;
            break;
        }
    }
    
    public void desactivarControles(){
        txtCodigoVacuna.setEditable(false);
        txtNombreVacuna.setEditable(false);
        txtDescripcion.setEditable(false);
        txtDosis.setEditable(false);
        txtFrecuencia.setEditable(false);
    }
    public void activarControles(){
        txtCodigoVacuna.setEditable(false);
        txtNombreVacuna.setEditable(true);
        txtDescripcion.setEditable(true);
        txtDosis.setEditable(true);
        txtFrecuencia.setEditable(true);
    }
    public void limpiarControles(){
        txtCodigoVacuna.clear();
        txtNombreVacuna.clear();
        txtDescripcion.clear();
        txtDosis.clear();
        txtFrecuencia.clear();
    }
    public boolean controlesVacios() {
    return !txtNombreVacuna.getText().isEmpty() && !txtDescripcion.getText().isEmpty() && !txtDosis.getText().isEmpty() && !txtFrecuencia.getText().isEmpty();
    }
    public boolean ValidarCorreo(String correo) {
        if (correo == null) return false;
        int atIndex = correo.indexOf("@");
        int dotIndex = correo.lastIndexOf(".");
        return atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < correo.length() - 1;
    }
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
}