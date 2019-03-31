package com.ibrhmdurna.chatapp.database.bridgeList;

public class ContentList extends Content {

    public ContentList(Implementor implementor) {
        super(implementor);
    }

    @Override
    public void viewList() {
        implementor.viewList();
    }
}
