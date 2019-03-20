package com.ibrhmdurna.chatapp.Utils;

public class File {
    private String path, title, image;
    private int count;

    public File(String title, String path, String image, int count){
        setTitle(title);
        setPath(path);
        setImage(image);
        setCount(count);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
