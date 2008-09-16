package org.qi4j.library.struts2.support.edit;

import static org.qi4j.library.struts2.util.ParameterizedTypes.findTypeVariables;

import org.qi4j.injection.scope.This;
import org.qi4j.library.struts2.support.ProvidesEntityOfMixin;

public abstract class ProvidesEditingOfMixin<T> extends ProvidesEntityOfMixin<T> implements ProvidesEditingOf<T> {
    
    @This ProvidesEditingOf<T> action;
    
    public void prepare() throws Exception {
        loadEntity();
    }

    public void prepareInput() throws Exception {
        
    }

    public String input() {
        if (getEntity() == null) {
            return "entity-not-found";
        }
        return INPUT;
    }

    @Override
    protected Class<T> typeToLoad() {
        return (Class<T>) findTypeVariables(action.getClass(), ProvidesEditingOf.class)[0];
    }
}