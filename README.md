# README

## To run

## Assumptions/Notes for Grader
### Information for Setting Up
1. JDK 17 is required (due to Android Gradle Plugin 8).

### Extra Information to Use the App
1. To access extra options for albums and photos (i.e. rename, move, delete, etc) tap/left click and hold, a popup menu should appear
2. If something a text edit is left empty in Search, then it is ignored (and consequently also the AND/OR selection). If both are left empty Search returns all photos
3. In the slideshow/imageviewer, tap the image to hide the HUD and enter fullscreen. Tap the visibility selector (the eye) to hide the details about tags and filename. Drag from left/right to move between photos.
4. Modifying a location tag to be empty effectively removes it

### Assumptions for Implementation
1. We store the photos in the app's local storage (so data/data/com.photos/files and/or data/users/0/com.photos/files).
    - This should not affect user experience unless you are actively looking for the photos.
    - Removing a photo from the source (like in Gallery) will not affect it.
    - If the photo is removed/deleted from the app, the file is also removed to save space.
2. If a photo is added such that its filename already exists, it will be renamed in the format name-num.ext (e.g. if photo.png exists, the new photo.png will be renamed photo-1.png).
    - This is out of consideration that duplicate filenames are quite common, e.g. image.png or unnamed.png.
3. Continuing 2, we will also assume it is allowed to have duplicate photos in an album.
    - There is no mention in the rubric about handling duplicates.
    - We may "duplicate", but in reality it is a new separate photo with its unique URI, file, and space on storage.

## Project Members
Jefferson Nguyen, Arnav Borborah