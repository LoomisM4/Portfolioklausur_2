package dhbw.rest;

import com.google.gson.Gson;
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
                         @RequestParam(value = "type") String type) {
        Gson gson = new Gson();
        SearchResult r = new SearchResult();
        List<SearchResultList> list = new ArrayList<>();

        if (type.equals("TRACK")) {
            String result = this.runRequest(RequestCategory.TRACK, suche);
            SearchTrack searchTrack = gson.fromJson(result, SearchTrack.class);
            searchTrack.getTracks().getItems().forEach(t -> {
                SearchResultList srl = new SearchResultList();
                srl.setId(t.getId());
                srl.setTitle(t.getName());
                srl.setDescription(t.getType());
                srl.setPlayLink(t.getHref());
                list.add(srl);
            });
        } else if (type.equals("ARTIST")) {
            String result = this.runRequest(RequestCategory.ARTIST, suche);
            SearchArtist searchArtist = gson.fromJson(result, SearchArtist.class);
            searchArtist.getArtists().getItems().forEach(a -> {
                SearchResultList srl = new SearchResultList();
                srl.setId(a.getId());
                srl.setTitle(a.getName());
                srl.setDescription(a.getType());
                srl.setPlayLink(a.getHref());
                list.add(srl);
            });
        } else if (type.equals("ALBUM")) {
            String result = this.runRequest(RequestCategory.ALBUM, suche);
            SearchAlbum searchAlbum = gson.fromJson(result, SearchAlbum.class);
            searchAlbum.getAlbums().getItems().forEach(a -> {
                SearchResultList srl = new SearchResultList();
                srl.setId(a.getId());
                srl.setTitle(a.getName());
                srl.setDescription(a.getType());
                srl.setPlayLink(a.getHref());
                list.add(srl);
            });
        }

        r.setResults(list);
        return gson.toJson(r);
    }

    private String runRequest(RequestCategory category, String search) {
        SpotifyRequest request = new SpotifyRequest(RequestType.SEARCH);
        Optional<String> resultOpt = null;
        try {
            resultOpt = request.performeRequestSearch(category, search);
        } catch (WrongRequestTypeException e) {
            e.printStackTrace();
        }

        AtomicReference<String> result = new AtomicReference<>(new String());
        resultOpt.ifPresent(result::set);

        return result.get();
    }
}
