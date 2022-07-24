/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto3.alg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jesús
 */
public class Proyecto3Alg {

       public static void main(String[] args) {
        String directorio = "data"; //Directiorio en el que tenemos las instancias: dado que esta dentro de la carpeta del proyecto, 
        //esta ruta sera valida independientemente del ordenador en el que estemos.
        String[] files = getFiles(directorio);
        

        for (int i = 0; i < files.length; i++) {
            float[][] matrizCoordenadasCiudades = leerFichero(files[i]);
            float[][] matrizDistancia = obtenerMatrizDistancia(matrizCoordenadasCiudades);
            System.out.println("Estamos en el fichero:" + (i + 1));
            
        //Nos indicara las ciudades que han sido visitadas o no en el backtracking    
        boolean[] camino = new boolean[matrizDistancia.length];

        // Ponemos la primera ciudad como visitada ya que empezaremos por ella 
        camino[0] = true;
        float distanciaMin = Integer.MAX_VALUE;//Inicializamos a este valor la distMin para que cualquier valor producido en las ejecuciones del backtrackin sea menor

       
        distanciaMin = backtracking(matrizDistancia, camino, 0, matrizDistancia.length, 1, 0, distanciaMin);//Como backtracking prueba todas las combinaciones la distanciaMin sera la menor de todos los caminos que puedan probarse es decir la distancia minima absoluta

        System.out.println("La mejor distancia con backtrackin  es "+distanciaMin);
 

            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }

    }

    /**
     * Con este metodo obtendremos cada uno de los ficheros en formato string
     *
     * @param dir string con el nombre del directorio en el que tenemos las
     * instancias
     * @return los un array de string con el nombre de cada uno de los ficheros.
     * Null en caso de pasar un directorio no valido
     */
    public static String[] getFiles(String dir) {
        File f = new File(dir); //Instanciamos f, que sera un objeto de tipo file que contendra el directorio en el que se encuentran nuestras instancias
        if (f.isDirectory()) {  //Comprobamos que es un directorio valido con un metodo de la clase file
            List<String> lista = new ArrayList(); //Creamos una lista
            File[] listaFicheros = f.listFiles(); //Creamos un array de ficheros de tipo file

            int size = listaFicheros.length;
            for (int i = 0; i < size; i++) { //Meteremos los ficheros de tipo file en una lista
                if (listaFicheros[i].isFile()) {
                    lista.add(listaFicheros[i].toString());
                }
            }
            String[] ficheros = lista.toArray(new String[0]); //Los sacamos como string para poder acceder a ellos posteriormente
            return ficheros;
        } else {
            System.out.println("Directorio no valido");
        }
        return null;
    }

    /**
     * Este metodo va a leer el fichero que le pasemos por parametro como string
     *
     * @param nombreFichero
     * @return
     */
    public static float[][] leerFichero(String nombreFichero) {

        //Declarar una variable BufferedReader
        BufferedReader br = null;
        try {
            //Crear un objeto BufferedReader al que se le pasa 
            //   un objeto FileReader con el nombre del fichero
            br = new BufferedReader(new FileReader(nombreFichero));
            //Leer la primera línea, guardando en un String
            String texto = br.readLine();
            //

            //Sacamos el numero de ciudades de la instancias que estamos leyendo
            int numeroCiudades = sacarNumeroCiudades(nombreFichero);
            float[][] matrizCiudades = new float[numeroCiudades][3]; //Nuestra matriz de ciudades contendra tantas filas como ciudades haya, y las columnas correspondaran a:
            // numero ciudad - coordenada x - coordenada y

            while (!texto.equalsIgnoreCase("NODE_COORD_SECTION")) {  //Nos situaremos en esta linea para poder proceder a leer la primera ciudad 
                texto = br.readLine();
            }
            // 

            int i = 0;
            while (i < numeroCiudades) {
                texto = br.readLine();
                matrizCiudades = obtenerMatrizCiudades(matrizCiudades, texto, i); //Leemos la linea y llamamos a obtenerMatrizCiudades, que nos devolvera 
                // una matriz con la linea leida y convertida del fichero a numero 
                i++;
            }
            return matrizCiudades;
        } catch (FileNotFoundException e) {
            System.out.println("Error: Fichero no encontrado");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error de lectura del fichero");
            System.out.println(e.getMessage());
        } finally { //finalmente, cerraremos el fichero. Tendreamos el cuenta que podemos obtener errores al cerrar el fichero, por lo que controlaremos excepciones
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                System.out.println("Error al cerrar el fichero");
                System.out.println(e.getMessage());
            }
        }

        //System.out.println(nombreFicheros.length);
        return null;
    }

    public static float[][] obtenerMatrizCiudades(float[][] m, String texto, int fila) {
        //  Ejemplo: 1 572 340
        //Saco el numero de la ciudad del texto que le pasamos por parametro y lo meto en la primera columna de la matriz
        int i = PosSinEspacio(texto);

        String StringSinEspacioComienzo = texto.substring(i); //Comenzando desde el primer caracter del numero de la ciudad en el que estemos
        //creamos un substring con los espacios quitados del principio
        //Ejemplo:   1 23 323 --> :1 23 323
        int indice = StringSinEspacioComienzo.indexOf(" ");   //Donde esta el siguiente espacio? Esto determinara hasta donde leer el numero
        float num = Float.parseFloat(StringSinEspacioComienzo.substring(0, indice)); //Creamos un substring con el numero de ciudad en el que estemos y parseamos para poder tenerlo en formato numerico

        m[fila][0] = num; // Dado que es el numero de ciudad en el que estamos, lo guardamos en la primera columna

        //Saco la coordenada x del texto que le pasamos por parametro y lo introducimos en la columna 1 de la matriz
        //en la fila que le pasamos por parametro, que correspondera con la fila de la ciudad
        //Haremos algo parecido a lo anterior pero ahora para sacar la coordenada x
        StringSinEspacioComienzo = StringSinEspacioComienzo.substring(indice + 1);

        i = PosSinEspacio(StringSinEspacioComienzo);
        String textoCoordenadas = StringSinEspacioComienzo.substring(i);

        indice = textoCoordenadas.indexOf(" ");

        num = Float.parseFloat(textoCoordenadas.substring(0, indice));

        m[fila][1] = num;

        //Saco la coordenada y del texto que le pasamos por parametro y lo introducimos en la columna 1 de la matriz
        //en la fila que le pasamos por parametro, que correspondera con la fila de la ciudad
        //Haremos algo parecido a lo anterior pero ahora para sacar la coordenada x
        textoCoordenadas = textoCoordenadas.substring(indice);

        i = PosSinEspacio(textoCoordenadas);

        String textoCoordenadaY = textoCoordenadas.substring(i);
        num = Float.parseFloat(textoCoordenadaY);
        m[fila][2] = num;

        return m; // devolvemos una matriz con la fila parseada a numero y con los numeros metidos en sus respectivas columnas

    }

    /**
     * Con este metodo sacaremos en que posicion empezamos a leer el numero de
     * ciudad en el que nos encontramos Esto lo realizamos dado que en algunos
     * casos sera: 1 En otros casos sera : 23 En otros :182 Incluso con
     * magnitudes mayores
     *
     * @param texto linea del fichero en la que estemos situados
     * @return posicion en la que empezamos a leer la ciudad
     */
    public static int PosSinEspacio(String texto) {
        int i = 0;
        char letra = texto.charAt(i);//
        boolean enc = false;
        while (!enc) {
            if (letra != ' ') {
                enc = true;
            } else {
                i++;
                letra = texto.charAt(i);
            }
        }
        return i;
    }

    /**
     * Este metodo lo usaremos para saber cuantas ciudades existen en la
     * instancia Sera importante a la hora de crear la matriz con la informacion
     * del numero de ciudad, coordenada x & coordenada y Cada fichero tendra
     * diferentes numero de ciudades, por lo que a la hora de instanciar la
     * matriz, querremos saber que tamanyo darle a las filas
     *
     * @param fichero
     * @return
     * @throws IOException
     */
    public static int sacarNumeroCiudades(String fichero) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fichero));
        String texto = br.readLine();

        while (!texto.substring(0, 9).equalsIgnoreCase("DIMENSION")) { //Nos situamos en la linea 9, que contiene una cadena de texto tal que asi: "DIMENSION: 280"
            texto = br.readLine();
        }
        int i = 0;
        while (texto.charAt(i) != ':') { //Sacamos la posicion de la cadena en la que tenemos los dos puntos, ya que nos interesa leer lo proximo: el numero de ciudades
            i++;
        }
        String dimensionSinDosPuntos = texto.substring(i + 1); //Creamos un substring comenzando en el siguiente caracter despues de los dos puntos

        i = PosSinEspacio(dimensionSinDosPuntos); //Obtenemos desde donde empezar a leer el numero (string)

        String digitosDimension = dimensionSinDosPuntos.substring(i); //Creamos un substring con el numero

        int tam = Integer.parseInt(digitosDimension); //Casteamos y lo obtenemos en formato int

        return tam;

    }

    /**
     * A partir de lo realizado anteriormente, sacaremos la matriz de distancia
     * de la instancia en la que nos encontremos en nuestro intento de
     * ahorrarnos tiempo de ejecución, tuvimos en cuenta que no iba a ser
     * necesario recorrer las nxn posiciones de la matriz: recorrimos solo los
     * elementos que se situaban por encima de la diagonal principal (m[i][j]),
     * y poniendo la misma distancia en la posición pero esta vez por debajo
     * (m[j][i]). La diagonal principal está llena de ceros.
     *
     * @param matrizfichero
     * @return
     */
    public static float[][] obtenerMatrizDistancia(float[][] matrizfichero) {
        float[][] matrizDistancia = new float[matrizfichero.length][matrizfichero.length];
        for (int i = 0; i < matrizfichero.length; i++) {
            float x1 = matrizfichero[i][1]; //coordenada x
            float y1 = matrizfichero[i][2]; //coordenada y
            for (int j = i; j < matrizfichero.length; j++) {
                if (i != j) {
                    float x2 = matrizfichero[j][1];
                    float y2 = matrizfichero[j][2];
                    float distanciaEuclidea = distEuclidea(x1, x2, y1, y2); //distancia entre la ciudad actual y todas las demas de la instancia
                    matrizDistancia[i][j] = distanciaEuclidea;
                    matrizDistancia[j][i] = distanciaEuclidea;
                } else {
                    matrizDistancia[i][j] = 0;
                }
            }
        }
        return matrizDistancia;
    }

    /**
     * Distancia euclidea entre dos ciudades
     *
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     * @return
     */
    public static float distEuclidea(float x1, float x2, float y1, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
      /**
     * Este metodo nos devolvera la distancia minima comenzando y acabando en la
     * primera ciudad de cada instancia proporcionada
     *
     * @param matrizDistancia Variable que representa la matriz de distancias de
     * la instancia que estemos recorriendo
     * @param camino Camino que va seleccionando segun la posicion de la pila en
     * la que estemos
     * @param posActual Varible para indicar la ciudad desde la cual vamos a
     * buscar la siguiente. Nos sera muy util en las llamadass recursivas
     * @param numCiudades Numero de ciudades de la instancia a la cual queramos
     * calcularle la distancia minimca
     * @param contador Contador para controlar cuantas ciudades hemos
     * seleccionado. Nos ayudara a controlar la llegada al caso base en cada una
     * de las llamadas
     * @param coste Coste actual del camino
     * @param distanciaMin Variable que se actualiza cuando llegamos al fin de
     * un camino en caso de que dicho camino sea menor al anterior minimo Sera
     * la variable que vayamos returnando. Esta variable la iniciamos al valor
     * maximo entero
     * @return varible de tipo float
     */
    static float backtracking(float[][] matrizDistancia, boolean[] camino, int posActual, int numCiudades, int contador, float coste, float distanciaMin) {
        //Caso base del metodo. Cuando el contador llegue al numero de ciudades, habremos completado un camino
        //y sumaremos el coste de ir de la ultima ciudad a la primera.
        //Como habremos completado un camino, tendremos el coste actual (sumando, como hemos dicho, el volver a la primera ciudad)
        //y lo compararemos con el camino minimo
        if (contador == numCiudades && matrizDistancia[posActual][0] > 0) { // la segunda condicion se pone para el caso de la primera fila, ahorrarnos una iteracion
            distanciaMin = Math.min(distanciaMin, coste + matrizDistancia[posActual][0]);
            return distanciaMin;
        }

        //Recorremos todas las ciudades buscando la siguiente no elegida desde la que estemos (posActual)
        for (int i = 0; i < numCiudades; i++) {
            //Sumamos el coste de la ciudad actual
            float costeActual = coste + matrizDistancia[posActual][i];
            if (costeActual > distanciaMin) { //Si el coste es mayor estricto que la distancia minima (ya que en caso de haber dos caminos 
                                              //iguales nos quedaremos con el primer camino)
                return distanciaMin;          //En caso de ser mayor en este momento, returnaremos, ya que por este camino no encontraremos un optimo
                                              //NOTA: en la documentacion, lo llamamos "podar". Entiendasen las llamadas recursivas en forma de arbol
            }
            //Si no hemos escogido la ciudad actual y esa ciudad es mayor que 0 (diagonal principal de la matriz de distancias), entonces:
            if (camino[i] == false && matrizDistancia[posActual][i] > 0) {
                //Marcamos la ciudad actual como visitada y repetimos el ciclo de escoger siguiente ciudad
                camino[i] = true;
                distanciaMin = backtracking(matrizDistancia, camino, i, numCiudades, contador + 1, costeActual, distanciaMin);
                //Marcamos la misma ciudad como no visitada. Asi, al volver al for con la siguiente ciudad, haremos el mismo paso pero
                //teniendo en cuenta el mismo camino sin escoger esta ciudad. Esto nos hara que recorramos todos los caminos posibles
                camino[i] = false;
            }
        }
        return distanciaMin;
    }
}



