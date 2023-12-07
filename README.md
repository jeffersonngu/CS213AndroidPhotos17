# README

## To run

## Assumptions/Notes for Grader
1. JDK 17 is required (due to Android Gradle Plugin 8)
2. We store the photos in the app's local storage (so data/users/com.photos/files)
3. If a photo is added such that its filename already exists, it will be renamed in the format name-num.ext (e.g. if photo.png exists, the new photo.png will be renamed photo-1.png)
    - This is out of consideration that duplicate filenames are quite common especially with CDNs or desktops/pcs, i.e. unnamed.png, image.png, etc
4. Continuing 3, we will also assume it is allowed to have duplicate photos in an album.
    - There is no mention in the rubric about handling duplicates.
    - We may "duplicate", but in reality it is a new separate photo with its unique URI, file, and space on storage.

## Project Members
Jefferson Nguyen, Arnav Borborah