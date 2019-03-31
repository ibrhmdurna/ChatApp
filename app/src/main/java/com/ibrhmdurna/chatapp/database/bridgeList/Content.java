package com.ibrhmdurna.chatapp.database.bridgeList;

public abstract class Content {

    Implementor implementor;

    Content(Implementor implementor){
        this.implementor = implementor;
    }

    public abstract void viewList();
}
