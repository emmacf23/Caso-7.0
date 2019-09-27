package com.company;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static  int cant_Tanteos = 0;
    private static String[] letras = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private static String[] numeros = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public static void setKey(String myKey) {
        MessageDigest sha;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String input, String key) {
        byte[] crypted = null;
        try {

            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();

        return encoder.encodeToString(crypted);
    }

    public static boolean decrypt(String input) {
        byte[] output = null;
        try {
            java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
            //SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            output = cipher.doFinal(decoder.decode(input));
            //System.out.println(new String(output));
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Combinacion> llenarCombinaciones() {
        ArrayList<Combinacion> combinaciones = new ArrayList<>();
        for (String letra : letras) {
            for (String numero : numeros) {
                combinaciones.add(new Combinacion(letra, numero));
            }
        }
        return combinaciones;

    }

    public static boolean probarKey(List<Combinacion> bloque) {
        String parteA = "29dh120";
        String parteB = "dk1";
        String secretMessage = "xZwM7BWIpSjYyGFr9rhpEa+cYVtACW7yQKmyN6OYSCv0ZEg9jWbc6lKzzCxRSSIvOvlimQZBMZOYnOwiA9yy3YU8zk4abFSItoW6Wj0ufQ0=";
        int cant_Muestra = (int) (bloque.size() * 0.6);
        Random random = new Random();
        for (int combinacion = 0; combinacion < cant_Muestra+1; combinacion++) {
            cant_Tanteos += 1;
            int randomPosition = random.nextInt(bloque.size());
            if (!(bloque.get(randomPosition).getLetra() + bloque.get(randomPosition).getNumero()).equals("b3")) {
                setKey(parteA + bloque.get(randomPosition).getLetra() + parteB + bloque.get(randomPosition).getNumero() + "3");
            }
            if (decrypt(secretMessage)) {

                return true;
            }
        }
        return false;
    }

    public static List<Combinacion> probabilidad(ArrayList<Combinacion> combinaciones) {
        int sizeParticiones = 4;
        int cuartaParte = combinaciones.size() / sizeParticiones;
        int end = cuartaParte;
        int inicio = 0;

        if (combinaciones.size() == 4) {
            return combinaciones;
        }

        ArrayList<Combinacion> bloque;
        for (int particion = 1; particion <= sizeParticiones; particion++) {
            bloque = new ArrayList<>(combinaciones.subList(inicio, end));
            if (probarKey(bloque)) {
                int length_bloque =  bloque.size();
                System.out.println("Bloque#" + particion);
                System.out.print("[");
                for (int posicion = 0; i <length_bloque; posicion++) {
                    System.out.print(bloque.get(posicion).getLetra() + bloque.get(posicion).getNumero() + ",");
                }
                System.out.println("]");
                return probabilidad(bloque);
            }
            inicio = end;
            end += cuartaParte;
        }
        return null;
    }

    public static void intentos(ArrayList<Combinacion> combinaciones , int intentos){
        for (int intento = 1; intento <= intentos; intento++) {
            System.out.println("Intento: " + intento);
            Collections.shuffle(combinaciones);
            List<Combinacion> bloque = probabilidad(combinaciones);
            if (bloque != null) {
                System.out.println("La respuesta es alguno de estos 4");
            } else {
                System.out.println("No se encontro la combinacion correcta!");
            }
            System.out.println("Cantidad de Tanteos: " + cant_Tanteos);
            System.out.println("-------------------------------");
            cant_Tanteos = 0;
        }
    }
    public static void main(String[] args) {
        ArrayList<Combinacion> combinaciones = llenarCombinaciones();
        int intentos = 20;
        intentos(combinaciones,intentos);
    }
}