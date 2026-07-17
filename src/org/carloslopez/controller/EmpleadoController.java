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
import org.carloslopez.bean.Empleado;
import org.carloslopez.db.Conexion;
import org.carloslopez.system.Principal;

public class EmpleadoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR,ELIMINAR,ACTUALIZAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Empleado> ListaEmpleado;
    
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnNuevo;
    @FXML private Button btnReporte;
    @FXML private TextField txtCodigoEmpleado;
    @FXML private TextField txtNombreEmpleado;
    @FXML private TextField txtApellidoEmpleado;
    @FXML private TextField txtCargo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TableView tblEmpleados;
    @FXML private TableColumn colCodigoEmpleado;
    @FXML private TableColumn colNombreEmpleado;
    @FXML private TableColumn colApellidoEmpleado;
    @FXML private TableColumn colCargoEmpleado;
    @FXML private TableColumn colTelefonoEmpleado;
    @FXML private TableColumn colCorreoEmpleado;
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
        txtTelefono.setTextFormatter(new TextFormatter<>(soloNumeros));
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblEmpleados.setItems(getEmpleado());
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado,Integer>("codigoEmpleado"));
        colNombreEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado,String>("nombreEmpleado"));
        colApellidoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado,String>("apellidoEmpleado"));
        colCargoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado,String>("cargo"));
        colTelefonoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado,String>("telefonoEmpleado"));
        colCorreoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado,String>("correoEmpleado"));
    }
    
    public void seleccionarElemento(){
        if (tblEmpleados.getSelectionModel().getSelectedItem() != null) {
            txtCodigoEmpleado.setText(String.valueOf(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
            txtNombreEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getNombreEmpleado());
            txtApellidoEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getApellidoEmpleado());
            txtCargo.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCargo());
            txtTelefono.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getTelefonoEmpleado());
            txtCorreo.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCorreoEmpleado());
        }else{
        JOptionPane.showMessageDialog(null, "No hay nada Seleccionado");
        }
    }
    
    public ObservableList<Empleado> getEmpleado(){
        ArrayList<Empleado> lista = new ArrayList<>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarEmpleados}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Empleado(
                    resultado.getInt("codigoEmpleado"),
                    resultado.getString("nombreEmpleado"),
                    resultado.getString("apellidoEmpleado"),
                    resultado.getString("cargo"),
                    resultado.getString("telefonoEmpleado"),
                    resultado.getString("correoEmpleado")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ListaEmpleado = FXCollections.observableArrayList(lista);
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
        Empleado registro = new Empleado();
        registro.setNombreEmpleado(txtNombreEmpleado.getText());
        registro.setApellidoEmpleado(txtApellidoEmpleado.getText());
        registro.setCargo(txtCargo.getText());
        registro.setTelefonoEmpleado(txtTelefono.getText());
        registro.setCorreoEmpleado(txtCorreo.getText());
        if (!ValidarCorreo(txtCorreo.getText())) {
            JOptionPane.showMessageDialog(null, "Ese Correo No es Valido, Reviselo Nuevamente");
            return;
        }
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarEmpleado(?, ?, ?, ?, ?)}");
            procedimiento.setString(1, registro.getNombreEmpleado());
            procedimiento.setString(2, registro.getApellidoEmpleado());
            procedimiento.setString(3, registro.getCargo());
            procedimiento.setString(4, registro.getTelefonoEmpleado());
            procedimiento.setString(5, registro.getCorreoEmpleado());
            procedimiento.execute();
            ListaEmpleado.add(registro);
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
                if(tblEmpleados.getSelectionModel().getSelectedItem() !=null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Empleado",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarEmpleado(?)}");
                            procedimiento.setInt(1, ((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
                            procedimiento.execute();
                            ListaEmpleado.remove(tblEmpleados.getSelectionModel().getSelectedIndex());
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
                if(tblEmpleados.getSelectionModel().getSelectedItem() != null){
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarEmpleado(?,?,?,?,?,?)}");
            Empleado registro = (Empleado)tblEmpleados.getSelectionModel().getSelectedItem();
            registro.setNombreEmpleado(txtNombreEmpleado.getText());
            registro.setApellidoEmpleado(txtApellidoEmpleado.getText());
            registro.setCargo(txtCargo.getText());
            registro.setTelefonoEmpleado(txtTelefono.getText());
            registro.setCorreoEmpleado(txtCorreo.getText());
            procedimiento.setInt(1, registro.getCodigoEmpleado());
            procedimiento.setString(2, registro.getNombreEmpleado());
            procedimiento.setString(3, registro.getApellidoEmpleado());
            procedimiento.setString(4, registro.getCargo());
            procedimiento.setString(5, registro.getTelefonoEmpleado());
            procedimiento.setString(6, registro.getCorreoEmpleado());
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
        txtCodigoEmpleado.setEditable(false);
        txtNombreEmpleado.setEditable(false);
        txtApellidoEmpleado.setEditable(false);
        txtCargo.setEditable(false);
        txtTelefono.setEditable(false);
        txtCorreo.setEditable(false);
    }
    public void activarControles(){
        txtCodigoEmpleado.setEditable(false);
        txtNombreEmpleado.setEditable(true);
        txtApellidoEmpleado.setEditable(true);
        txtCargo.setEditable(true);
        txtTelefono.setEditable(true);
        txtCorreo.setEditable(true);
    }
    public void limpiarControles(){
        txtCodigoEmpleado.clear();
        txtNombreEmpleado.clear();
        txtApellidoEmpleado.clear();
        txtCargo.clear();
        txtTelefono.clear();
        txtCorreo.clear();
    }
    public boolean controlesVacios() {
        return !txtNombreEmpleado.getText().isEmpty() && !txtApellidoEmpleado.getText().isEmpty() && !txtCargo.getText().isEmpty() && !txtTelefono.getText().isEmpty() && !txtCorreo.getText().isEmpty();
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
