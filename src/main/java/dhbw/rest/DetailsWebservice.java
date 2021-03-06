package dhbw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dhbw.pojo.detail.album.DetailsAlbum;
import dhbw.pojo.detail.artist.DetailsArtist;
import dhbw.pojo.detail.track.DetailsTrack;
import dhbw.pojo.result.detail.DetailResult;
import dhbw.spotify.RequestCategory;
import dhbw.spotify.RequestType;
import dhbw.spotify.SpotifyRequest;
import dhbw.spotify.WrongRequestTypeException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Marcel Wettach
 */
@RestController
public class DetailsWebservice {
    @RequestMapping("/detail/{id}")
    public String details(@PathVariable("id") String id, @RequestParam("type") String type) throws IOException, WrongRequestTypeException {
        ObjectMapper mapper = new ObjectMapper();

        DetailResult dr = new DetailResult();

        if (type.equals("TRACK")) {
            String result = this.runRequest(RequestCategory.TRACK, id);
            DetailsTrack searchTrack = mapper.readValue(result, DetailsTrack.class);
            dr.setTitle(searchTrack.getName());
            dr.setInfo(searchTrack.getType());
        } else if (type.equals("ARTIST")) {
            String result = this.runRequest(RequestCategory.ARTIST, id);
            DetailsArtist searchArtist = mapper.readValue(result, DetailsArtist.class);
            dr.setTitle(searchArtist.getName());
            dr.setInfo(searchArtist.getType());
        } else if (type.equals("ALBUM")) {
            String result = this.runRequest(RequestCategory.ALBUM, id);
            DetailsAlbum searchAlbum = mapper.readValue(result, DetailsAlbum.class);
            dr.setTitle(searchAlbum.getName());
            dr.setInfo(searchAlbum.getType());
        }

        return mapper.writeValueAsString(dr);
    }

    private String runRequest(RequestCategory category, String search) throws WrongRequestTypeException {
        SpotifyRequest request = new SpotifyRequest(RequestType.DETAIL);
        Optional<String> resultOpt = request.performeRequestDetail(category, search);

        AtomicReference<String> result = new AtomicReference<>(new String());
        resultOpt.ifPresent(result::set);

        return result.get();
    }
}
