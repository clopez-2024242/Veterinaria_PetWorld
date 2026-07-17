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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.carloslopez.bean.Veterinario;
import org.carloslopez.db.Conexion;
import org.carloslopez.system.Principal;

public class VeterinarioController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR,ELIMINAR,ACTUALIZAR,NINGUNO};
    private operaciones tipoDeOperaciones = operaciones.NINGUNO;
    private ObservableList<Veterinario> ListaVeterinario;
    private DatePicker fecha;
    
    @FXML private GridPane grpFechaIngreso;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnNuevo;
    @FXML private Button btnReporte;
    @FXML private TextField txtCodigoVeterinario;
    @FXML private TextField txtNombreVeterinario;
    @FXML private TextField txtApellidoVeterinario;
    @FXML private TextField txtEspecialidadVeterinario;
    @FXML private TextField txtTelefonoVeterinario;
    @FXML private ComboBox cbxEstado;
    @FXML private TableView tblVeterinarios;
    @FXML private TableColumn colCodigoVeterinario;
    @FXML private TableColumn colNombreVeterinario;
    @FXML private TableColumn colApellidoVeterinario;
    @FXML private TableColumn colEspecialidad;
    @FXML private TableColumn colTelefonoVeterinario;
    @FXML private TableColumn colEstado;
    @FXML private TableColumn colFechaIngreso;
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
        txtTelefonoVeterinario.setTextFormatter(new TextFormatter<>(soloNumeros));
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/carloslopez/resource/cliente.css");
        fecha.setDisable(true);
        grpFechaIngreso.add(fecha, 2, 3);
        cbxEstado.getItems().addAll("Activo","Inactivo");
    }
    
    public void cargarDatos(){
        tblVeterinarios.setItems(getVeterinario());
        colCodigoVeterinario.setCellValueFactory(new PropertyValueFactory<Veterinario, Integer>("codigoVeterinario"));
        colNombreVeterinario.setCellValueFactory(new PropertyValueFactory<Veterinario,String>("nombreVeterinario"));
        colApellidoVeterinario.setCellValueFactory(new PropertyValueFactory<Veterinario,String>("apellidoVeterinario"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<Veterinario,String>("especialidad"));
        colTelefonoVeterinario.setCellValueFactory(new PropertyValueFactory<Veterinario,String>("telefonoVeterinario"));
        colEstado.setCellValueFactory(new PropertyValueFactory<Veterinario,String>("estado"));
        colFechaIngreso.setCellValueFactory(new PropertyValueFactory<Veterinario,Date>("fechaIngreso"));
    }
    
    public void seleccionarElemento(){
        if (tblVeterinarios.getSelectionModel().getSelectedItem() != null) {
            txtCodigoVeterinario.setText(String.valueOf((((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getCodigoVeterinario())));
            txtNombreVeterinario.setText(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getNombreVeterinario());
            txtApellidoVeterinario.setText(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getApellidoVeterinario());
            txtEspecialidadVeterinario.setText(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getEstado());
            txtTelefonoVeterinario.setText(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getTelefonoVeterinario());
            cbxEstado.setValue(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getEstado());
            fecha.selectedDateProperty().set((((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getFechaIngreso()));
        }else{
            JOptionPane.showMessageDialog(null, "No hay nada Seleccionado");
        }
    }
    
    public ObservableList<Veterinario> getVeterinario(){
        ArrayList<Veterinario> lista = new ArrayList<>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarVeterinarios}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Veterinario(
                        resultado.getInt("codigoVeterinario"),
                        resultado.getString("nombreVeterinario"),
                        resultado.getString("apellidoVeterinario"),
                        resultado.getString("especialidad"),
                        resultado.getString("telefonoVeterinario"),
                        resultado.getString("estado"),
                        resultado.getDate("fechaIngreso")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ListaVeterinario = FXCollections.observableArrayList(lista);
    }
    
    public void nuevo(){
        switch(tipoDeOperaciones){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                imgNuevo.setImage(new Image("/org/carloslopez/image/PerroBotonGuardar.png"));
                imgEliminar.setImage(new Image("/org/carloslopez/image/PerroBotonCancelar.png"));
                tipoDeOperaciones = operaciones.GUARDAR;
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
                    tipoDeOperaciones = operaciones.NINGUNO;
                    cargarDatos();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe rellenar todos los datos");
                }
            break;
        }
    }
    
    public void guardar(){
        Veterinario registro = new Veterinario();
        registro.setNombreVeterinario(txtNombreVeterinario.getText());
        registro.setApellidoVeterinario(txtApellidoVeterinario.getText());
        registro.setEspecialidad(txtEspecialidadVeterinario.getText());
        registro.setTelefonoVeterinario(txtTelefonoVeterinario.getText());
        registro.setEstado(cbxEstado.getValue().toString());
        registro.setFechaIngreso(fecha.getSelectedDate());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarVeterinario(?,?,?,?,?,?)}");
            procedimiento.setString(1, registro.getNombreVeterinario());
            procedimiento.setString(2, registro.getApellidoVeterinario());
            procedimiento.setString(3, registro.getEspecialidad());
            procedimiento.setString(4, registro.getTelefonoVeterinario());
            procedimiento.setString(5, registro.getEstado());
            procedimiento.setDate(6, new java.sql.Date(registro.getFechaIngreso().getTime()));
            procedimiento.execute();
            ListaVeterinario.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperaciones){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/carloslopez/image/PerroBotonAdd.png"));
                imgEliminar.setImage(new Image("/org/carloslopez/image/PerroBotonRemove.png"));
                tipoDeOperaciones = operaciones.NINGUNO;
            break;
            case NINGUNO:
                if(tblVeterinarios.getSelectionModel().getSelectedItem() !=null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Cliente",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarVeterinario(?)}");
                            procedimiento.setInt(1, ((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getCodigoVeterinario());
                            procedimiento.execute();
                            ListaVeterinario.remove(tblVeterinarios.getSelectionModel().getSelectedIndex());
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
        switch(tipoDeOperaciones){
            case NINGUNO:
                if(tblVeterinarios.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    imgEditar.setImage(new Image("/org/carloslopez/image/PerroBotonGuardar.png"));
                    imgReporte.setImage(new Image("/org/carloslopez/image/PerroBotonCancelar.png"));
                    activarControles();
                    tipoDeOperaciones = operaciones.ACTUALIZAR;
                }
            break;
            case ACTUALIZAR:
                actualizar();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("/org/carloslopez/image/PerroBotonEdit.png"));
                imgReporte.setImage(new Image("/org/carloslopez/image/PerroBotonReport.png"));
                cargarDatos();
                tipoDeOperaciones = operaciones.NINGUNO;
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarVeterinario(?,?,?,?,?,?,?)}");
            Veterinario registro = (Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem();
            registro.setNombreVeterinario(txtNombreVeterinario.getText());
            registro.setApellidoVeterinario(txtApellidoVeterinario.getText());
            registro.setEspecialidad(txtEspecialidadVeterinario.getText());
            registro.setTelefonoVeterinario(txtTelefonoVeterinario.getText());
            registro.setEstado(cbxEstado.getValue().toString());
            registro.setFechaIngreso(fecha.getSelectedDate());
            procedimiento.setInt(1, registro.getCodigoVeterinario());
            procedimiento.setString(2, registro.getNombreVeterinario());
            procedimiento.setString(3, registro.getApellidoVeterinario());
            procedimiento.setString(4, registro.getEspecialidad());
            procedimiento.setString(5, registro.getTelefonoVeterinario());
            procedimiento.setString(6, registro.getEstado());
            procedimiento.setDate(7, new java.sql.Date(registro.getFechaIngreso().getTime()));
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void generarReporte(){
        switch(tipoDeOperaciones){
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
                tipoDeOperaciones = operaciones.NINGUNO;
            break;
        }
    }
    
    public void desactivarControles(){
        txtCodigoVeterinario.setEditable(false);
        txtNombreVeterinario.setEditable(false);
        txtApellidoVeterinario.setEditable(false);
        txtEspecialidadVeterinario.setEditable(false);
        txtTelefonoVeterinario.setEditable(false);
        cbxEstado.setEditable(false);
        fecha.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoVeterinario.setEditable(false);
        txtNombreVeterinario.setEditable(true);
        txtApellidoVeterinario.setEditable(true);
        txtEspecialidadVeterinario.setEditable(true);
        txtTelefonoVeterinario.setEditable(true);
        cbxEstado.setEditable(true);
        fecha.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoVeterinario.clear();
        txtNombreVeterinario.clear();
        txtApellidoVeterinario.clear();
        txtEspecialidadVeterinario.clear();
        txtTelefonoVeterinario.clear();
        cbxEstado.getSelectionModel().select(null);
        fecha.setSelectedDate(null);
    }
    
    public boolean controlesVacios() {
    return !txtNombreVeterinario.getText().isEmpty() && !txtApellidoVeterinario.getText().isEmpty() && !txtEspecialidadVeterinario.getText().isEmpty() && !txtTelefonoVeterinario.getText().isEmpty() && !cbxEstado.getValue().toString().isEmpty();
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
