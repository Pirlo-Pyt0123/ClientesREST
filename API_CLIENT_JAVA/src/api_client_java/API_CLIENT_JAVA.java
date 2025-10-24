/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package api_client_java;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 *
 * @author elmer
 */
public class API_CLIENT_JAVA {

    /**
     * @param args the command line arguments
     */
    static String BASE_URL = "http://localhost:8000/api";
    static String jwtToken = "";

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== LOGIN ===");
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        String loginPayload = "{\"email\":\"" + email + "\",\"password\":\"" + pass + "\"}";
        String loginResponse = sendPOST(BASE_URL + "/login", loginPayload, null);
        System.out.println("Respuesta login: " + loginResponse);
        jwtToken = extractToken(loginResponse);

        System.out.println("Token JWT extraído: [" + jwtToken + "]");

        if(jwtToken == null) {
            System.out.println("No se pudo obtener el token.");
            return;
        }

        
        while(true){
            System.out.println("\n--- Menu CRUD Personas ---");
            System.out.println("1. Listar personas (GET)");
            System.out.println("2. Crear persona (POST)");
            System.out.println("3. Editar persona (PUT)");
            System.out.println("4. Eliminar persona (DELETE)");
            System.out.println("0. Salir");
            System.out.print("Elegir: ");
            int op = Integer.parseInt(sc.nextLine());

            if(op==0) break;

            switch(op){
                case 1: 
                    String personas = sendGET(BASE_URL + "/personas", jwtToken);
                    System.out.println(personas);
                    break;
                case 2: 
                    System.out.print("Nombres: ");
                    String nombres = sc.nextLine();
                    System.out.print("Apellidos: ");
                    String apellidos = sc.nextLine();
                    System.out.print("CI: ");
                    String ci = sc.nextLine();
                    System.out.print("Dirección: ");
                    String direccion = sc.nextLine();
                    System.out.print("Teléfono: ");
                    String telefono = sc.nextLine();
                    System.out.print("Email: ");
                    String emailP = sc.nextLine();
                    String payload = String.format(
                        "{\"nombres\":\"%s\",\"apellidos\":\"%s\",\"ci\":\"%s\",\"direccion\":\"%s\",\"telefono\":\"%s\",\"email\":\"%s\"}",
                        nombres, apellidos, ci, direccion, telefono, emailP);
                    String postResp = sendPOST(BASE_URL + "/personas", payload, jwtToken);
                    System.out.println("Respuesta: " + postResp);
                    break;
                case 3: 
                    System.out.print("ID a editar: ");
                    String idEdit = sc.nextLine();
                    System.out.print("Nuevos nombres: ");
                    String nombresEdit = sc.nextLine();
                    System.out.print("Nuevos apellidos: ");
                    String apellidosEdit = sc.nextLine();
                    System.out.print("Nuevo CI: ");
                    String ciEdit = sc.nextLine();
                    System.out.print("Nueva dirección: ");
                    String direccionEdit = sc.nextLine();
                    System.out.print("Nuevo teléfono: ");
                    String telefonoEdit = sc.nextLine();
                    System.out.print("Nuevo email: ");
                    String emailEdit = sc.nextLine();
                    String putPayload = String.format(
                        "{\"nombres\":\"%s\",\"apellidos\":\"%s\",\"ci\":\"%s\",\"direccion\":\"%s\",\"telefono\":\"%s\",\"email\":\"%s\"}",
                        nombresEdit, apellidosEdit, ciEdit, direccionEdit, telefonoEdit, emailEdit);
                    String putResp = sendPUT(BASE_URL + "/personas/" + idEdit, putPayload, jwtToken);
                    System.out.println("Respuesta: " + putResp);
                    break;
                case 4: 
                    System.out.print("ID a eliminar: ");
                    String idDel = sc.nextLine();
                    String delResp = sendDELETE(BASE_URL + "/personas/" + idDel, jwtToken);
                    System.out.println("Respuesta: " + delResp);
                    break;
            }
        }
        System.out.println("Programa finalizado.");
    }

    static String extractToken(String json) {
        int i = json.indexOf("\"token\":\"");
        if(i==-1) return null;
        i += 9;
        int f = json.indexOf("\"", i);
        if(f==-1) return null;
        return json.substring(i, f);
    }

    static String sendPOST(String url, String payload, String token) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        if(token != null)
            con.setRequestProperty("Authorization", "Bearer " + token);
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(payload.getBytes("UTF-8"));
        os.close();

        InputStream is;
        try{
            is = con.getInputStream();
        }catch(IOException e){
            is = con.getErrorStream();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    static String sendGET(String url, String token) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        if(token != null)
            con.setRequestProperty("Authorization", "Bearer " + token);

        InputStream is;
        try{
            is = con.getInputStream();
        }catch(IOException e){
            is = con.getErrorStream();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    static String sendPUT(String url, String payload, String token) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        if(token != null)
            con.setRequestProperty("Authorization", "Bearer " + token);
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(payload.getBytes("UTF-8"));
        os.close();

        InputStream is;
        try{
            is = con.getInputStream();
        }catch(IOException e){
            is = con.getErrorStream();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    static String sendDELETE(String url, String token) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("DELETE");
        if(token != null)
            con.setRequestProperty("Authorization", "Bearer " + token);

        InputStream is;
        try{
            is = con.getInputStream();
        }catch(IOException e){
            is = con.getErrorStream();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

}
