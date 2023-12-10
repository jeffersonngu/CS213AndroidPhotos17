package com.photos.database;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.photos.models.Album;
import com.photos.models.Photo;
import com.photos.util.PhotosFileUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Handles our Inserts and Transactions
 */
@Dao
public abstract class AlbumsDao {

    private Context context;

    /**
     * Pass a context to help with Transactions
     * @param context Any context, preferably the Application's Context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertAlbumInfo(Album.AlbumInfo albumInfo);

    @Update
    public abstract void updateAlbumInfo(Album.AlbumInfo albumInfo);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertPhoto(Photo photo);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long[] insertPhotoList(List<Photo> photoList);

    @Update
    public abstract void updatePhotoList(List<Photo> photoList);

    @Transaction
    public boolean insertAlbum(Album album) {
        boolean insertAlbumInfo = !(insertAlbumInfo(album.getAlbumInfo()) < 0);
        boolean insertPhotoList = Arrays.stream(insertPhotoList(album.getPhotoList()))
                .noneMatch(value -> value < 0);
        return insertAlbumInfo && insertPhotoList;
    }

    @Transaction
    public void updateAlbum(Album album) {
        updateAlbumInfo(album.getAlbumInfo());
        updatePhotoList(album.getPhotoList());
    }

    @Query("UPDATE albums SET name = :newName WHERE id = :albumId AND NOT EXISTS (SELECT 1 FROM albums WHERE name = :newName AND id != :albumId)")
    public abstract int renameAlbum(int albumId, String newName);

    @Query("UPDATE photos SET location = :newLocation WHERE id = :photoId")
    public abstract void photoSetLocation(int photoId, String newLocation);

    @Query("UPDATE photos SET people = :newPeople WHERE id = :photoId")
    abstract void photoUpdatePeopleSet(int photoId, Set<String> newPeople);

    @Transaction
    public boolean photoPeopleSetAdd(Photo photo, String newPerson) {
        Set<String> people = photo.getPeople();
        if (people.add(newPerson)) {
            photoUpdatePeopleSet(photo.getId(), people);
            return true;
        } else {
            return false;
        }
    }

    @Transaction
    public void photoPeopleSetRemove(Photo photo, String person) {
        Set<String> people = photo.getPeople();
        if (people.remove(person)) {
            photoUpdatePeopleSet(photo.getId(), people);
        }
    }

    private void deletePhotoFile(Photo photo) {
        context.deleteFile(PhotosFileUtils.getFileName(context, photo.getUri()));
    }

    @Delete
    abstract void deleteAlbumInfo(Album.AlbumInfo albumInfo);

    @Delete
    abstract void deletePhotoEntity(Photo photo);

    @Transaction
    public void deletePhoto(Photo photo) {
        deletePhotoFile(photo);
        deletePhotoEntity(photo);
    }

    @Transaction
    public void deleteAlbum(Album album) {
        album.getPhotoList().forEach(this::deletePhotoFile);
        deleteAlbumInfo(album.getAlbumInfo()); /* Because we use onDelete CASCADE we do not have to worry about Photo */
    }

    @Query("UPDATE photos SET albumId = :albumId WHERE id = :photoId")
    public abstract void movePhoto(int photoId, int albumId);

    @Transaction
    @Query("SELECT * FROM albums")
    public abstract LiveData<List<Album>> getAllAlbums();

    @Transaction
    @Query("SELECT * FROM albums")
    public abstract List<Album> getAllAlbumsNoLive();

    @Query("SELECT * FROM photos WHERE albumId = :albumId")
    public abstract LiveData<List<Photo>> getPhotoList(int albumId);

    @Query("SELECT * FROM photos")
    public abstract LiveData<List<Photo>> getPhotoList();

    @Query("SELECT * FROM photos WHERE location COLLATE NOCASE = :location")
    public abstract LiveData<List<Photo>> getPhotoListFromLocation(String location);

    /*
     * Hacky trick, since we store the Set<String> as a JSONArray of ["data1", "data2", ...]
     * we can simply match it to ":person", this should sanitize it as well
     */
    @Query("SELECT * FROM photos WHERE people LIKE '%\"' || :person || '\"%'")
    public abstract LiveData<List<Photo>> getPhotoListFromPerson(String person);

    @Query("SELECT * FROM photos WHERE location COLLATE NOCASE = :location AND people LIKE '%\"' || :person || '\"%'")
    public abstract LiveData<List<Photo>> getPhotoListFromLocationAndPerson(String location, String person);

    @Query("SELECT * FROM photos WHERE location COLLATE NOCASE = :location OR people LIKE '%\"' || :person || '\"%'")
    public abstract LiveData<List<Photo>> getPhotoListFromLocationOrPerson(String location, String person);
}
