package mx.uv;


import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import mx.uv.Controller.DAO;
import static spark.Spark.*;
import  com.google.gson.*;

public class App {
    static Gson gson = new Gson();
    public static void main(String[] args) {
        // Registrar archivos MP3
        FileUploadController fileUploadController = new FileUploadController();

        // Registrar rutas de subida de archivos
        fileUploadController.registerRoutes();

        //fuente:https://gist.github.com/saeidzebardast/e375b7d17be3e0f4dddf
        options("/*",(request,response)->{
            String accessControlRequestHeaders=request.headers("Access-Control-Request-Headers");
            if(accessControlRequestHeaders!=null){
                response.header("Access-Control-Allow-Headers",accessControlRequestHeaders);
            }
            String accessControlRequestMethod=request.headers("Access-Control-Request-Method");
            if(accessControlRequestMethod!=null){
                response.header("Access-Control-Allow-Methods",accessControlRequestMethod);
                }
            return "OK";
        });
        post("/canciones", (request, response) -> {
            // Obtener información de la solicitud y crear una nueva canción
            String payload = request.body();
            Song newSong = gson.fromJson(payload, Song.class);

            // Insertar la nueva canción en la base de datos
            boolean success = DAO.insertSong(newSong);

            // Crear y devolver una respuesta
            JsonObject respuesta = new JsonObject();
            respuesta.addProperty("msj", success);
            return respuesta;
        });


        // Nueva funcionalidad para trabajar con Microsoft Graph
        final Properties oAuthProperties = new Properties();
        try {
            oAuthProperties.load(App.class.getResourceAsStream("oAuth.properties"));
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
            return;
        }

        initializeGraph(oAuthProperties);

        Scanner input = new Scanner(System.in);

        int choice = -1;

        while (choice != 0) {
            System.out.println("Please choose one of the following options:");
            System.out.println("0. Exit");
            System.out.println("1. Display access token");
            System.out.println("2. Call Microsoft Graph");

            try {
                choice = input.nextInt();
            } catch (InputMismatchException ex) {
                // Skip over non-integer input
            }

            try {
                callMicrosoftGraph(oAuthProperties);
            } catch (URISyntaxException e) {
                System.out.println("Invalid URI syntax: " + e.getMessage());
            }
        }

        input.close();
    }

    // Otras funciones existentes...

    private static void callMicrosoftGraph(Properties properties) throws URISyntaxException {
        try {
            // Reemplaza "YOUR_ACCESS_TOKEN" con el token de acceso que obtuviste durante la autenticación.
            String accessToken = "YOUR_ACCESS_TOKEN";

            // URL para obtener el perfil del usuario actual (puedes ajustarla según tus necesidades)
            URI uri = new URI("https://graph.microsoft.com/v1.0/me");
            URL url = uri.toURL();

            // Abrir conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configurar la solicitud
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            // Obtener la respuesta
            int responseCode = connection.getResponseCode();
            System.out.println("Código de respuesta: " + responseCode);

            // Leer la respuesta
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                // Imprimir la respuesta JSON
                System.out.println("Respuesta del servidor:");
                System.out.println(response.toString());
            } else {
                System.out.println("Error al realizar la solicitud. Código de respuesta: " + responseCode);
            }

            // Cerrar la conexión
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeGraph(Properties properties) {
        // Implementación de la inicialización de Microsoft Graph
    }

}
