/*
Programador:Carlos Enrique Lopez Quino
Codigo tecnico:IN5AV
Carnet:2024242
Fecha de creacion: 07-04-2025
Fecha de Modificacion:
07-04-2025
21-04-2025
22-04-2025
*/
package org.carloslopez.system;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.carloslopez.controller.ClienteController;
import org.carloslopez.controller.EmpleadoController;
import org.carloslopez.controller.MainController;
import org.carloslopez.controller.ProgramadorController;
import org.carloslopez.controller.ProveedorController;
import org.carloslopez.controller.VacunaController;
import org.carloslopez.controller.VeterinarioController;

public class Principal extends Application{
    private final String PAQUETE_VISTA = "/org/carloslopez/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    public static void main(String [] args){
        launch(args);
    }

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        this.escenarioPrincipal = escenarioPrincipal;
        //Parent root = FXMLLoader.load(getClass().getResource("/org/carloslopez/view/MainView.fxml"));
        //Scene escena = new Scene(root);
        escenarioPrincipal.setTitle("PetWorld -- Carlos Enrique Lopez Quino");
        escenarioPrincipal.getIcons().add(new Image("/org/carloslopez/image/PetWorldMini.png"));
        //escenarioPrincipal.setScene(escena);
        menuPrincipal();
        escenarioPrincipal.show();
    }
    public void menuPrincipal(){
        try{
            MainController menu = (MainController)cambiarEscena("MainView.fxml",850,500);
            menu.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void ventanaProgramador(){
        try{
            ProgramadorController programador = (ProgramadorController)cambiarEscena("ProgramadorView.fxml", 600,400);
            programador.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaCliente(){
        try{
            ClienteController vistaCliente = (ClienteController)cambiarEscena("ClienteView.fxml",750,500);
            vistaCliente.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaVeterinario(){
        try{
            VeterinarioController vistaVeterinario = (VeterinarioController)cambiarEscena("VeterinarioView.fxml",850,500);
            vistaVeterinario.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProveedor(){
        try{
            ProveedorController vistaProveedor = (ProveedorController)cambiarEscena("ProveedorView.fxml",800,500);
            vistaProveedor.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaVacuna(){
        try{
            VacunaController vistaVacuna = (VacunaController)cambiarEscena("VacunaView.fxml",800,500);
            vistaVacuna.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEmpleado(){
        try{
            EmpleadoController vistaEmpleado = (EmpleadoController)cambiarEscena("EmpleadoView.fxml",850,500);
            vistaEmpleado.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
        return resultado;
    }
}
