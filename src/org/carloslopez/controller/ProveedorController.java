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
import org.carloslopez.bean.Proveedor;
import org.carloslopez.db.Conexion;
import org.carloslopez.system.Principal;


public class ProveedorController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR,ELIMINAR,ACTUALIZAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Proveedor> ListaProveedor;
    
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnNuevo;
    @FXML private Button btnReporte;
    @FXML private TextField txtCodigoProveedor;
    @FXML private TextField txtNombreProveedor;
    @FXML private TextField txtDireccionProveedor;
    @FXML private TextField txtTelefonoProveedor;
    @FXML private TextField txtCorreoProveedor;
    @FXML private TableColumn colCodigoProveedor;
    @FXML private TableColumn colNombreProveedor;
    @FXML private TableColumn colDireccionProveedor;
    @FXML private TableColumn colTelefonoProveedor;
    @FXML private TableColumn colCorreoProveedor;
    @FXML private TableView tblProveedores;
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
        txtTelefonoProveedor.setTextFormatter(new TextFormatter<>(soloNumeros));
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblProveedores.setItems(getProveedor());
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor,Integer>("codigoProveedor"));
        colNombreProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor,String>("nombreProveedor"));
        colDireccionProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor,String>("direccionProveedor"));
        colTelefonoProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor,String>("telefonoProveedor"));
        colCorreoProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor,String>("correoProveedor"));
    }
    
    public void seleccionarElemento(){
        if (tblProveedores.getSelectionModel().getSelectedItem() != null) {
            txtCodigoProveedor.setText(String.valueOf(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getCodigoProveedor()));
            txtNombreProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getNombreProveedor());
            txtDireccionProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getDireccionProveedor());
            txtTelefonoProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getTelefonoProveedor());
            txtCorreoProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getCorreoProveedor());
        }else{
            JOptionPane.showMessageDialog(null, "No hay nada Seleccionado");
        }
    }
    
    public ObservableList<Proveedor> getProveedor(){
        ArrayList<Proveedor> lista = new ArrayList<>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarProveedores}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Proveedor(
                    resultado.getInt("codigoProveedor"),
                    resultado.getString("nombreProveedor"),
                    resultado.getString("direccionProveedor"),
                    resultado.getString("telefonoProveedor"),
                    resultado.getString("correoProveedor")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ListaProveedor = FXCollections.observableArrayList(lista);
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
        Proveedor registro = new Proveedor();
        registro.setNombreProveedor(txtNombreProveedor.getText());
        registro.setDireccionProveedor(txtDireccionProveedor.getText());
        registro.setTelefonoProveedor(txtTelefonoProveedor.getText());
        registro.setCorreoProveedor(txtCorreoProveedor.getText());
        if (!ValidarCorreo(txtCorreoProveedor.getText())) {
            JOptionPane.showMessageDialog(null, "Ese Correo No es Valido, Reviselo Nuevamente");
            return;
        }
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarProveedor(?,?,?,?)}");
            procedimiento.setString(1, registro.getNombreProveedor());
            procedimiento.setString(2, registro.getDireccionProveedor());
            procedimiento.setString(3, registro.getTelefonoProveedor());
            procedimiento.setString(4, registro.getCorreoProveedor());
            procedimiento.execute();
            ListaProveedor.add(registro);
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
            case NINGUNO:
                if(tblProveedores.getSelectionModel().getSelectedItem() !=null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Cliente",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarProveedor(?)}");
                            procedimiento.setInt(1, ((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getCodigoProveedor());
                            procedimiento.execute();
                            ListaProveedor.remove(tblProveedores.getSelectionModel().getSelectedIndex());
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
                if(tblProveedores.getSelectionModel().getSelectedItem() != null){
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarProveedor(?,?,?,?,?)}");
            Proveedor registro = (Proveedor)tblProveedores.getSelectionModel().getSelectedItem();
            registro.setNombreProveedor(txtNombreProveedor.getText());
            registro.setDireccionProveedor(txtDireccionProveedor.getText());
            registro.setTelefonoProveedor(txtTelefonoProveedor.getText());
            registro.setCorreoProveedor(txtCorreoProveedor.getText());
            procedimiento.setInt(1, registro.getCodigoProveedor());
            procedimiento.setString(2, registro.getNombreProveedor());
            procedimiento.setString(3, registro.getDireccionProveedor());
            procedimiento.setString(4, registro.getTelefonoProveedor());
            procedimiento.setString(5, registro.getCorreoProveedor());
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
        txtCodigoProveedor.setEditable(false);
        txtNombreProveedor.setEditable(false);
        txtDireccionProveedor.setEditable(false);
        txtTelefonoProveedor.setEditable(false);
        txtCorreoProveedor.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoProveedor.setEditable(false);
        txtNombreProveedor.setEditable(true);
        txtDireccionProveedor.setEditable(true);
        txtTelefonoProveedor.setEditable(true);
        txtCorreoProveedor.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoProveedor.clear();
        txtNombreProveedor.clear();
        txtDireccionProveedor.clear();
        txtTelefonoProveedor.clear();
        txtCorreoProveedor.clear();
    }
    
    public boolean controlesVacios() {
    return !txtNombreProveedor.getText().isEmpty() && !txtDireccionProveedor.getText().isEmpty() && !txtTelefonoProveedor.getText().isEmpty() && !txtCorreoProveedor.getText().isEmpty();
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
