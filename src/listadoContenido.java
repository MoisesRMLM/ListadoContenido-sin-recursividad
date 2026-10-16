// Importamos Path y Files del paquete java.nio.file.
//
// Path representa una ruta de un fichero o directorio.
// Files contiene métodos para trabajar con esas rutas.
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.AccessDeniedException;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.Date;
import java.util.Scanner;

public class listadoContenido {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Sacamos el path de la carpeta del usuario
        Path dir = Path.of(System.getProperty("user.home"));

        System.out.println("Ubicación: " + dir.normalize());

        System.out.println("Indica el máximo de archivos a mostrar con la búsqueda:");
        int max = sc.nextInt();

        try {
            listado(dir, max);
        } catch (IOException e) {
            System.err.println("Error: " + e);
        } // try-catch
    } // main

    static void listado(Path rutaBase, int max) throws IOException{
        try (Stream<Path> ficheros = Files.walk(rutaBase)) {
            ficheros.limit(max).forEach(f -> { // Limit se encarga de solo tomar los max primeros resultados de la lista
                try {
                    if (!Files.isHidden(f)) {
                        if (Files.isRegularFile(f)) {
                            System.out.print(f.getFileName());

                            // Imprime la fecha de la última modificación
                            Date fecha = new Date(Files.getLastModifiedTime(f).toMillis());
                            System.out.print(" | Fecha: " + fecha);

                            // Imprime el propietario
                            System.out.print(" | Propietario: " + Files.getOwner(f));

                            // Peso del archivo
                            System.out.println(" | Tamaño: " + ajustarTamanio(Files.size(f)));

                        // Si es un directorio
                        } else if (Files.isDirectory(f)) {
                            System.out.println(f.getFileName());
                        } // if-else
                    } // if
                } catch (AccessDeniedException e) {
                    // De esta forma se evita que se imprima AccessDeniedException en algunos archivos, el catch está vacio para que no imprima nada
                } catch (IOException e) {
                    System.err.println("Error: " + e);
                } // try-catch
            });
                
        } catch (IOException e) {
            System.err.println("Error: " + e);
        } // try-catch
    } // listado

    // Para ajustar las unidades, se intenta dividir entre 1000 hasta que el resultado fuese menor que 1
    // Por cada división que se ha podido realizar, se pasa la  unidad a una superior, hasta llegar a los gb
    static String ajustarTamanio(double tamanio) {
        int unidad = 0; // 0: B, 1: kB, 2: MB, 3: GB

        while (tamanio / 1000 >= 1 && unidad < 3) {
            tamanio /= 1000;
            unidad ++;
        } // while

        switch (unidad) {
            case 0: return tamanio + "B";
            case 1: return tamanio + "kB";
            case 2: return tamanio + "MB";
            case 3: return tamanio + "GB";
            default: return "Error al calcular el tamaño"; // Nunca se llega a este caso, pero es necesario para que java compile
        } // switch
    } // ajustarTamanio
} // listadoContenido