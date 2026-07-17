package org.carloslopez.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.carloslopez.bean.Cliente;
import org.carloslopez.db.Conexion;
import org.carloslopez.system.Principal;


public class ClienteController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR,ELIMINAR,ACTUALIZAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Cliente> ListaCliente;
    private DatePicker fecha;
    
    @FXML private GridPane grpFechaRegistro;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnNuevo;
    @FXML private Button btnReporte;
    @FXML private TextField txtCodigoCliente;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtApellidoCliente;
    @FXML private TextField txtTelefonoCliente;
    @FXML private TextField txtDireccionCliente;
    @FXML private TextField txtCorreoCliente;
    @FXML private TableView tblClientes;
    @FXML private TableColumn colCodigoCliente;
    @FXML private TableColumn colNombreCliente;
    @FXML private TableColumn colApellidoCliente;
    @FXML private TableColumn colTelefonoCliente;
    @FXML private TableColumn colDireccionCliente;
    @FXML private TableColumn colCorreoCliente;
    @FXML private TableColumn colFechaRegistro;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private ImageView imgEliminar;
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
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
        txtTelefonoCliente.setTextFormatter(new TextFormatter<>(soloNumeros));
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/carloslopez/resource/cliente.css");
        fecha.setDisable(true);
        grpFechaRegistro.add(fecha, 2, 3);
    }
    
    public void cargarDatos(){
        tblClientes.setItems(getCliente());
        colCodigoCliente.setCellValueFactory(new PropertyValueFactory<Cliente,Integer>("codigoCliente"));
        colNombreCliente.setCellValueFactory(new PropertyValueFactory<Cliente,String>("nombreCliente"));
        colApellidoCliente.setCellValueFactory(new PropertyValueFactory<Cliente,String>("apellidoCliente"));
        colTelefonoCliente.setCellValueFactory(new PropertyValueFactory<Cliente,Integer>("telefonoCliente"));
        colDireccionCliente.setCellValueFactory(new PropertyValueFactory<Cliente,String>("direccionCliente"));
        colCorreoCliente.setCellValueFactory(new PropertyValueFactory<Cliente,String>("correoCliente"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<Cliente,Date>("fechaRegistro"));
    }
    
    public void seleccionarElemento(){
        if (tblClientes.getSelectionModel().getSelectedItem() != null) {
            txtCodigoCliente.setText(String.valueOf(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCodigoCliente()));
            txtNombreCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getNombreCliente());
            txtApellidoCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getApellidoCliente());
            txtTelefonoCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getTelefonoCliente());
            txtDireccionCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getDireccionCliente());
            txtCorreoCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCorreoCliente());
            fecha.selectedDateProperty().set(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getFechaRegistro());
        }else{
        JOptionPane.showMessageDialog(null, "No hay nada Seleccionado");
        }
    }
    
    public ObservableList<Cliente> getCliente(){
        ArrayList<Cliente> lista = new ArrayList<>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarClientes}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Cliente(
                        resultado.getInt("codigoCliente"),
                        resultado.getString("NombreCliente"),
                        resultado.getString("apellidoCliente"),
                        resultado.getString("telefonoCliente"),
                        resultado.getString("direccionCliente"),
                        resultado.getString("correoCliente"),
                        resultado.getDate("fechaRegistro")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ListaCliente = FXCollections.observableArrayList(lista);
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
                guardar();
                desactivarControles();
                limpiarControles();
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/carloslopez/image/PerroBotonAdd.png"));
                imgEliminar.setImage(new Image("/org/carloslopez/image/PerroBotonRemove.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
            break;
        }
    }
    
    public void guardar(){
        Cliente registro = new Cliente();
        //registro.setCodigoCliente(Integer.parseInt(txtCodigoCliente.getText()));
        registro.setNombreCliente(txtNombreCliente.getText());
        registro.setApellidoCliente(txtApellidoCliente.getText());
        registro.setTelefonoCliente(txtTelefonoCliente.getText());
        registro.setDireccionCliente(txtDireccionCliente.getText());
        registro.setCorreoCliente(txtCorreoCliente.getText());
        registro.setFechaRegistro(fecha.getSelectedDate());
        if (!ValidarCorreo(txtCorreoCliente.getText())) {
            JOptionPane.showMessageDialog(null, "Ese Correo No es Valido, Reviselo Nuevamente");
            return;
        }
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarCliente(?, ?, ?, ?, ?, ?)}");
            procedimiento.setString(1, registro.getNombreCliente());
            procedimiento.setString(2, registro.getApellidoCliente());
            procedimiento.setString(3, registro.getTelefonoCliente());
            procedimiento.setString(4, registro.getDireccionCliente());
            procedimiento.setString(5, registro.getCorreoCliente());
            procedimiento.setDate(6, new java.sql.Date(registro.getFechaRegistro().getTime()));
            procedimiento.execute();
            ListaCliente.add(registro);
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
                if(tblClientes.getSelectionModel().getSelectedItem() !=null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Cliente",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarCliente(?)}");
                            procedimiento.setInt(1, ((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCodigoCliente());
                            procedimiento.execute();
                            ListaCliente.remove(tblClientes.getSelectionModel().getSelectedIndex());
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
                if(tblClientes.getSelectionModel().getSelectedItem() != null){
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarCliente(?,?,?,?,?,?,?)}");
            Cliente registro = (Cliente)tblClientes.getSelectionModel().getSelectedItem();
            registro.setNombreCliente(txtNombreCliente.getText());
            registro.setApellidoCliente(txtApellidoCliente.getText());
            registro.setTelefonoCliente(txtTelefonoCliente.getText());
            registro.setDireccionCliente(txtDireccionCliente.getText());
            registro.setCorreoCliente(txtCorreoCliente.getText());
            registro.setFechaRegistro(fecha.getSelectedDate());
            procedimiento.setInt(1, registro.getCodigoCliente());
            procedimiento.setString(2, registro.getNombreCliente());
            procedimiento.setString(3, registro.getApellidoCliente());
            procedimiento.setString(4, registro.getTelefonoCliente());
            procedimiento.setString(5, registro.getDireccionCliente());
            procedimiento.setString(6, registro.getCorreoCliente());
            procedimiento.setDate(7, new java.sql.Date(registro.getFechaRegistro().getTime()));
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
        txtCodigoCliente.setEditable(false);
        txtNombreCliente.setEditable(false);
        txtApellidoCliente.setEditable(false);
        txtTelefonoCliente.setEditable(false);
        txtDireccionCliente.setEditable(false);
        txtCorreoCliente.setEditable(false);
        fecha.setDisable(true);
    }
    public void activarControles(){
        txtCodigoCliente.setEditable(false);
        txtNombreCliente.setEditable(true);
        txtApellidoCliente.setEditable(true);
        txtTelefonoCliente.setEditable(true);
        txtDireccionCliente.setEditable(true);
        txtCorreoCliente.setEditable(true);
        fecha.setDisable(false);
    }
    public void limpiarControles(){
        txtCodigoCliente.clear();
        txtNombreCliente.clear();
        txtApellidoCliente.clear();
        txtTelefonoCliente.clear();
        txtDireccionCliente.clear();
        txtCorreoCliente.clear();
        fecha.setSelectedDate(null);
    }
    public boolean controlesVacios() {
        return !txtNombreCliente.getText().isEmpty() && !txtApellidoCliente.getText().isEmpty() && !txtTelefonoCliente.getText().isEmpty() && !txtDireccionCliente.getText().isEmpty() && !txtCorreoCliente.getText().isEmpty();
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
