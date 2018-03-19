package dhbw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dhbw.pojo.result.search.SearchResult;
import dhbw.pojo.result.search.SearchResultList;
import dhbw.pojo.search.album.SearchAlbum;
import dhbw.pojo.search.artist.SearchArtist;
import dhbw.pojo.search.track.SearchTrack;
import dhbw.spotify.RequestCategory;
import dhbw.spotify.RequestType;
import dhbw.spotify.SpotifyRequest;
import dhbw.spotify.WrongRequestTypeException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Marcel Wettach
 */
@RestController
public class SearchWebservice {
    @RequestMapping("/search")
    public String search(@RequestParam(value = "query") String suche,
                         @RequestParam(value = "type") String type) throws IOException, WrongRequestTypeException {
        ObjectMapper mapper = new ObjectMapper();
        SearchResult r = new SearchResult();
        List<SearchResultList> list = new ArrayList<>();
        RequestCategory category = RequestCategory.valueOf(type);

        if (category.equals(RequestCategory.TRACK)) {
            String result = this.runRequest(RequestCategory.TRACK, suche);
            SearchTrack searchTrack = mapper.readValue(result, SearchTrack.class);
            searchTrack.getTracks().getItems().forEach(t -> {
                SearchResultList srl = new SearchResultList();
                srl.setId(t.getId());
                srl.setTitle(t.getName());
                srl.setDescription(t.getType());
                srl.setPlayLink(t.getUri());
                list.add(srl);
            });
        } else if (category.equals(RequestCategory.ARTIST)) {
            String result = this.runRequest(RequestCategory.ARTIST, suche);
            SearchArtist searchArtist = mapper.readValue(result, SearchArtist.class);
            searchArtist.getArtists().getItems().forEach(a -> {
                SearchResultList srl = new SearchResultList();
                srl.setId(a.getId());
                srl.setTitle(a.getName());
                srl.setDescription(a.getType());
                srl.setPlayLink(a.getUri());
                list.add(srl);
            });
        } else if (category.equals(RequestCategory.ALBUM)) {
            String result = this.runRequest(RequestCategory.ALBUM, suche);
            SearchAlbum searchAlbum = mapper.readValue(result, SearchAlbum.class);
            searchAlbum.getAlbums().getItems().forEach(a -> {
                SearchResultList srl = new SearchResultList();
                srl.setId(a.getId());
                srl.setTitle(a.getName());
                srl.setDescription(a.getType());
                srl.setPlayLink(a.getUri());
                list.add(srl);
            });
        }

        r.setResults(list);
        return mapper.writeValueAsString(r);
    }

    private String runRequest(RequestCategory category, String search) throws WrongRequestTypeException {
        SpotifyRequest request = new SpotifyRequest(RequestType.SEARCH);
        Optional<String> resultOpt = request.performeRequestSearch(category, search);
        AtomicReference<String> result = new AtomicReference<>(new String());
        resultOpt.ifPresent(result::set);

        return result.get();
    }
}
