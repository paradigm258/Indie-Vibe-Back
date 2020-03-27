package com.swp493.ivb.features.browse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.swp493.ivb.common.mdata.DTOGenre;
import com.swp493.ivb.common.mdata.ServiceMasterData;
import com.swp493.ivb.common.playlist.DTOPlaylistSimple;
import com.swp493.ivb.common.playlist.ServicePlaylist;
import com.swp493.ivb.common.release.DTOReleaseSimple;
import com.swp493.ivb.common.release.ServiceRelease;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceBrowseImpl implements ServiceBrowse {

    @Autowired
    ServiceRelease releaseService;

    @Autowired
    ServiceMasterData masterDataService;

    @Autowired
    ServicePlaylist playlistService;

    @Override
    public Map<String, Object> getGeneral(String userId) {
        Map<String, Object> res = new HashMap<>();
        res.put("releases", releaseService.getLastest(userId));
        List<DTOGerneCollection<DTOPlaylistSimple>> list = masterDataService.getGenreList().stream().map(
            genre ->{
                DTOGerneCollection<DTOPlaylistSimple> result = new DTOGerneCollection<>();
                result.setGenre(genre);
                result.setItems(playlistService.getGenrePlaylists(genre.getId(), userId, 0, 4).getItems());
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

    DTOGerneCollection<DTOReleaseSimple> getGenreCollection(String userId, DTOGenre genre) {
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
        int limit = 4;
        Map<String, Object> res = new HashMap<>();
        res.put("genre", masterDataService.getGenre(genreId));
        res.put("playlists", playlistService.getGenrePlaylists(userId, genreId, offset, limit).getItems());
        res.put("releases", releaseService.getReleaseGenre(genreId, userId, offset, limit).getItems());
        return res;
    }

}
