package com.swp493.ivb.features.browse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.swp493.ivb.common.artist.ServiceArtist;
import com.swp493.ivb.common.mdata.DTOGenre;
import com.swp493.ivb.common.mdata.ServiceMasterData;
import com.swp493.ivb.common.playlist.DTOPlaylistSimple;
import com.swp493.ivb.common.playlist.ServicePlaylist;
import com.swp493.ivb.common.release.DTOReleaseSimple;
import com.swp493.ivb.common.release.ServiceRelease;
import com.swp493.ivb.common.track.ServiceTrack;
import com.swp493.ivb.common.user.ServiceUser;
import com.swp493.ivb.features.workspace.ITypeAndId;
import com.swp493.ivb.features.workspace.RepositoryPlayRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ServiceBrowseImpl implements ServiceBrowse {

    @Autowired
    ServiceRelease releaseService;

    @Autowired
    ServiceTrack trackService;

    @Autowired
    ServiceMasterData masterDataService;

    @Autowired
    ServicePlaylist playlistService;

    @Autowired
    ServiceArtist artistService;

    @Autowired
    ServiceUser userService;

    @Autowired
    RepositoryPlayRecord playStatRepo;

    @Override
    public Map<String, Object> getGeneral(String userId) {
        Map<String, Object> res = new HashMap<>();
        res.put("releases", releaseService.getLastest(userId));
        List<DTOGerneCollection<DTOPlaylistSimple>> list = masterDataService.getGenreList().stream().map(
            genre ->{
                DTOGerneCollection<DTOPlaylistSimple> result = new DTOGerneCollection<>();
                result.setGenre(genre);
                result.setItems(playlistService.getGenrePlaylists(genre.getId(), userId, 0, 6).getItems());
                return result;
            }   
        ).collect(Collectors.toList());
        res.put("playlists", list);
        return res;
    }

    @Override
    public List<DTOGerneCollection<DTOReleaseSimple>> getReleaseGenreCollections(String userId) {
        List<DTOGenre> genres = masterDataService.getGenreList();
        return genres.stream().map(g -> getGenreCollection(userId, g)).collect(Collectors.toList());
    }

    public DTOGerneCollection<DTOReleaseSimple> getGenreCollection(String userId, DTOGenre genre) {
        DTOGerneCollection<DTOReleaseSimple> res = new DTOGerneCollection<>();
        res.setGenre(genre);
        res.setItems(releaseService.getReleaseGenre(genre.getId(), userId, 0, 4).getItems());
        return res;
    }

    @Override
    public Map<String, Object> getGenreRelease(String userId, String genreId, int offset, int limit) {
        Map<String, Object> res = new HashMap<>();
        res.put("genre", masterDataService.getGenre(genreId));
        res.put("data", releaseService.getReleaseGenre(genreId, userId, offset, limit));
        return res;
    }

    @Override
    public Map<String, Object> getGenrePlaylist(String userId, String genreId, int offset, int limit) {
        Map<String, Object> res = new HashMap<>();
        res.put("genre", masterDataService.getGenre(genreId));
        res.put("data", playlistService.getGenrePlaylists(genreId, userId, offset, limit));
        return res;
    }

    @Override
    public Map<String, Object> getGenre(String userId, String genreId) {
        int offset = 0;
        int limit = 6;
        Map<String, Object> res = new HashMap<>();
        res.put("genre", masterDataService.getGenre(genreId));
        res.put("playlists", playlistService.getGenrePlaylists(genreId, userId, offset, limit).getItems());
        res.put("releases", releaseService.getReleaseGenre(genreId, userId, offset, limit).getItems());
        return res;
    }

    @Override
    public Map<String, Object> getHome(String userId) {
        Map<String, Object> res = new HashMap<>();
        res.put("myPlaylists", playlistService.getPlaylists(userId, userId, 0, 6, "own").getItems());
        res.put("myArtists", artistService.getArtists(userId, userId, 0, 6).getItems());
        res.put("recent", getRecent(userId));
        res.put("most", getMost(userId));
        res.put("newReleases", releaseService.getLastest(userId));
        res.put("popularReleases", releaseService.getPopular(userId));
        return res;
    }

    @Override
    public List<Object> getRecent(String userId) {
        return constructData(playStatRepo.findRecent(userId, PageRequest.of(0, 6)), userId);
    }

    @Override
    public List<Object> getMost(String userId) {
        return constructData(playStatRepo.findMost(userId, PageRequest.of(0, 6)), userId);
    }

    private List<Object> constructData(List<ITypeAndId> list, String userId){
        return list.stream().map(item ->{
            switch (item.getObjectType()) {
                case "release":
                    return releaseService.getReleaseSimple(item.getObjectId(), userId);
                case "playlist":
                    return playlistService.getPlaylistSimple(item.getObjectId(), userId);
                case "artist":
                    return artistService.getArtistFull(userId, item.getObjectId());
                default:
                    return null; 
            }
        }).filter(item -> item != null)
                .collect(Collectors.toList());
    }

}
