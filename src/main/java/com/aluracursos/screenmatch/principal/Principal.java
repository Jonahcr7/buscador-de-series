package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import com.aluracursos.screenmatch.service.consumoAPI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private consumoAPI consumoApi = new consumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=f7ebf773&";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraElMenu() {
        System.out.println("Por favor escribe el nombre de la serie que deseas buscar: ");
        // Busca los datos generales de la serie
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);

        // Buscamos los datos de todas las temporadas de toda la serie
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i <= datos.totalDeTemporadas() ; i++) {
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporadas);

        }
        //temporadas.forEach(System.out::println);

        // Mostramos únicamente los nombres de los episodios de todas las temporadas
        /*for (int i = 0; i < datos.totalDeTemporadas(); i++) {
            List<DatosEpisodio> episodioTemporada = temporadas.get(i).episodios();
            for (int j = 0; j < episodioTemporada.size(); j++) {
                System.out.println(episodioTemporada.get(j).titulo());
            }
        } */

        // Mismo código que el de arriba comentado pero utilizando forEach
        //temporadas.forEach(t -> t.episodios().forEach(e-> System.out.println(e.titulo())));

        // Convertir toda la información a una lista  de datosEpisodio
        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        // Top 5 episodios
        datosEpisodios.stream()
                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
                .limit(5)
                .forEach(System.out::println);

    }
}
