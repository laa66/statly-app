package com.laa66.statlyapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.ItemRecentlyPlayed;
import com.laa66.statlyapp.model.ItemTopArtists;
import com.laa66.statlyapp.model.ItemTopTracks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotifyAPIServiceImplTest {

    @Mock
    @Qualifier("restTemplateInterceptor")
    RestTemplate restTemplate;

    @InjectMocks
    SpotifyAPIServiceImpl spotifyAPIService;

    @Test
    void shouldGetCurrentUser() {
        UserIdDTO dto = new UserIdDTO("1");
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER),
                eq(HttpMethod.GET), any(), eq(UserIdDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        UserIdDTO returnDto = spotifyAPIService.getCurrentUser();
        assertEquals(dto.getId(), returnDto.getId());
    }

    @Test
    void shouldGetTopTracksWithValidUrl() {
        TopTracksDTO dto = new TopTracksDTO(List.of(new ItemTopTracks()
                , new ItemTopTracks()), "2");
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        TopTracksDTO returnDto = spotifyAPIService.getTopTracks("user"
                , SpotifyAPI.TOP_TRACKS + "long_term");
        assertEquals(dto.getItemTopTracks().size(), returnDto.getItemTopTracks().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
    }

    @Test
    void shouldGetTopTracksWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "_t"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
        TopTracksDTO returnDto = spotifyAPIService.getTopTracks("user"
                , SpotifyAPI.TOP_TRACKS + "_t");
        assertNull(returnDto);
    }

    @Test
    void shouldGetTopArtistsWithValidUrl() {
        TopArtistsDTO dto = new TopArtistsDTO("1", List.of(new ItemTopArtists(), new ItemTopArtists()));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        TopArtistsDTO returnDto = spotifyAPIService.getTopArtists("user"
                , SpotifyAPI.TOP_ARTISTS + "long_term");
        assertEquals(dto.getItemTopArtists().size(), returnDto.getItemTopArtists().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
    }

    @Test
    void shouldGetTopArtistsWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS + "_t"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
        TopArtistsDTO returnDto = spotifyAPIService.getTopArtists("user"
                , SpotifyAPI.TOP_ARTISTS + "_t");
        assertNull(returnDto);
    }

    @Test
    void shouldGetMainstreamScoreWithValidUrl() {
        ItemTopTracks track1 = new ItemTopTracks();
        track1.setPopularity(40);
        ItemTopTracks track2 = new ItemTopTracks();
        track2.setPopularity(70);
        TopTracksDTO dto = new TopTracksDTO(List.of(track1, track2), "2");
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        MainstreamScoreDTO returnDto = spotifyAPIService.getMainstreamScore("user"
                , SpotifyAPI.TOP_TRACKS + "long_term");
        assertEquals(55.00, returnDto.getScore());
    }

    @Test
    void shouldGetMainStreamScoreWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "_t"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
        MainstreamScoreDTO returnDto = spotifyAPIService.getMainstreamScore("user"
                , SpotifyAPI.TOP_TRACKS + "_t");
        assertNull(returnDto);
    }

    @Test
    void shouldGetTopGenresWithValidUrl() {
        ItemTopArtists artist1 = new ItemTopArtists();
        artist1.setGenres(List.of("classic", "rock"));
        ItemTopArtists artist2 = new ItemTopArtists();
        artist2.setGenres(List.of("classic"));
        TopArtistsDTO dto = new TopArtistsDTO();
        dto.setItemTopArtists(List.of(artist1, artist2));

        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        TopGenresDTO returnDto = spotifyAPIService.getTopGenres("user"
                , SpotifyAPI.TOP_ARTISTS + "long_term");

        assertEquals(2, returnDto.getGenres().size());
        assertEquals(2, returnDto.getGenres().get("classic"));
        assertEquals(1, returnDto.getGenres().get("rock"));
    }

    @Test
    void shouldGetTopGenresWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS + "_t"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
        TopGenresDTO returnDto = spotifyAPIService.getTopGenres("user"
                , SpotifyAPI.TOP_ARTISTS + "_t");
        assertNull(returnDto);
    }

    @Test
    void shouldGetRecentlyPlayed() {
        RecentlyPlayedDTO dto = new RecentlyPlayedDTO("2", List.of(new ItemRecentlyPlayed(), new ItemRecentlyPlayed()));
        when(restTemplate.exchange(eq(SpotifyAPI.RECENTLY_PLAYED_TRACKS),
                eq(HttpMethod.GET), any(), eq(RecentlyPlayedDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        RecentlyPlayedDTO returnDto = spotifyAPIService.getRecentlyPlayed("user");
        assertEquals(dto.getItemRecentlyPlayedList().size(), returnDto.getItemRecentlyPlayedList().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
    }

    @Test
    void shouldPostTopTracksPlaylistWithValidUrl() {
        UserIdDTO userIdDTO = new UserIdDTO("1");
        TopTracksDTO tracksDTO = new TopTracksDTO(List.of(new ItemTopTracks()
                , new ItemTopTracks()), "2");
        UserIdDTO playlistIdDTO = new UserIdDTO("10");
        String snapshotId = "snapshotId";

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER),
                eq(HttpMethod.GET), any(), eq(UserIdDTO.class)))
                .thenReturn(new ResponseEntity<>(userIdDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(tracksDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.CREATE_TOP_PLAYLIST.replace("user_id",
                userIdDTO.getId())), eq(HttpMethod.POST), any(), eq(UserIdDTO.class)))
                .thenReturn(new ResponseEntity<>(playlistIdDTO, HttpStatus.CREATED));
        when(restTemplate.exchange(eq(SpotifyAPI.ADD_PLAYLIST_TRACK.replace("playlist_id",
                playlistIdDTO.getId())), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(snapshotId, HttpStatus.CREATED));
        assertEquals(snapshotId, spotifyAPIService.postTopTracksPlaylist("user", SpotifyAPI.TOP_TRACKS + "long_term"));

    }

    @Test
    void shouldPostTopTracksPlaylistWithNotValidUrl() {
        UserIdDTO userIdDTO = new UserIdDTO("1");
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER),
                eq(HttpMethod.GET), any(), eq(UserIdDTO.class)))
                .thenReturn(new ResponseEntity<>(userIdDTO, HttpStatus.OK));
        assertThrows(SpotifyAPIException.class, () -> spotifyAPIService.postTopTracksPlaylist("user", "wrong_term"));
    }

    @Test
    void shouldPostTopTracksPlaylistWithValidUrlIfPostingFailed() {
        UserIdDTO userIdDTO = new UserIdDTO("1");
        TopTracksDTO tracksDTO = new TopTracksDTO(List.of(new ItemTopTracks()
                , new ItemTopTracks()), "2");
        UserIdDTO playlistIdDTO = new UserIdDTO("10");

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER),
                eq(HttpMethod.GET), any(), eq(UserIdDTO.class)))
                .thenReturn(new ResponseEntity<>(userIdDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(tracksDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.CREATE_TOP_PLAYLIST.replace("user_id",
                userIdDTO.getId())), eq(HttpMethod.POST), any(), eq(UserIdDTO.class)))
                .thenReturn(new ResponseEntity<>(playlistIdDTO, HttpStatus.CREATED));
        when(restTemplate.exchange(eq(SpotifyAPI.ADD_PLAYLIST_TRACK.replace("playlist_id",
                playlistIdDTO.getId())), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

        assertNull(spotifyAPIService.postTopTracksPlaylist("user", SpotifyAPI.TOP_TRACKS + "long_term"));
    }
}