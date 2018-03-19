package dhbw.rest;

import com.google.gson.Gson;
import dhbw.pojo.detail.album.DetailsAlbum;
import dhbw.pojo.detail.artist.DetailsArtist;
import dhbw.pojo.detail.track.DetailsTrack;
import dhbw.pojo.result.detail.DetailResult;
import dhbw.pojo.search.album.SearchAlbum;
import dhbw.pojo.search.artist.SearchArtist;
import dhbw.pojo.search.track.SearchTrack;
import dhbw.spotify.RequestCategory;
import dhbw.spotify.RequestType;
import dhbw.spotify.SpotifyRequest;
import dhbw.spotify.WrongRequestTypeException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Marcel Wettach
 */
@RestController
public class DetailsWebservice {
    @RequestMapping("/details/{id}")
    public String details(@PathVariable String id, @RequestParam("type") String type) {
        Gson gson = new Gson();

        DetailResult dr = new DetailResult();

        if (type.equals("TRACK")) {
            String result = this.runRequest(RequestCategory.TRACK, id);
            DetailsTrack searchTrack = gson.fromJson(result, DetailsTrack.class);
            dr.setTitle(searchTrack.getName());
            dr.setInfo(searchTrack.getType());
        } else if (type.equals("ARTIST")) {
            String result = this.runRequest(RequestCategory.ARTIST, id);
            DetailsArtist searchArtist = gson.fromJson(result, DetailsArtist.class);
            dr.setTitle(searchArtist.getName());
            dr.setInfo(searchArtist.getType());
        } else if (type.equals("ALBUM")) {
            String result = this.runRequest(RequestCategory.ALBUM, id);
            DetailsAlbum searchAlbum = gson.fromJson(result, DetailsAlbum.class);
            dr.setTitle(searchAlbum.getName());
            dr.setInfo(searchAlbum.getType());
        }

        return gson.toJson(dr);
    }

    private String runRequest(RequestCategory category, String search) {
        SpotifyRequest request = new SpotifyRequest(RequestType.DETAIL);
        Optional<String> resultOpt = null;
        try {
            resultOpt = request.performeRequestDetail(category, search);
        } catch (WrongRequestTypeException e) {
            e.printStackTrace();
        }

        AtomicReference<String> result = new AtomicReference<>(new String());
        resultOpt.ifPresent(result::set);

        return result.get();
    }
}
