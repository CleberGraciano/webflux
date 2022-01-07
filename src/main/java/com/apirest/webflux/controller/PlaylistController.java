package com.apirest.webflux.controller;

import com.apirest.webflux.document.Playlist;
import com.apirest.webflux.services.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.swing.plaf.TableUI;
import java.time.Duration;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public Flux<Playlist> getPlaylist(){
        return playlistService.findAll();
    }

    @GetMapping(value = "/{id}")
    public Mono<Playlist> getPlaylistId(@PathVariable String id){
        return playlistService.findById(id);
    }

    @PostMapping
    public Mono<Playlist> savePlalist(@RequestBody Playlist playlist){
        return playlistService.save(playlist);
    }

    //Cliente quando acionar vai ser criado um fluxo de intervalos e tambem vai buscar nosso fluxo de eventos no banco de dados
    //Vamos devolver para cada intervalo de tempo uma playlist no intervalo de tempo, até passar por todas que está no banco vai mostrar uma a uma
    //na programação reativa funciona da seguinte maneira eu faço uma chamada e quando chamar outra mesmo que a outra não terminou ela não bloqueia a anterios porque ela
    // é não bloqueante
    //reativo trabalhamos com tempos assincronos e não bloqueantes ou seja o servidor recebe varias requisições e vai processando conforme vai chegando,
    //a primeira pode estar em processamento ainda e ele vai respondendo as outras
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Tuple2<Long, Playlist>> getPlaylistByEvents(){
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(10)); //Intervalo de cada resposta que vamos enviar para o cliente
        Flux<Playlist> events = playlistService.findAll();
        System.out.println("Passou aqui events");
        return Flux.zip(interval, events);

    }


}
