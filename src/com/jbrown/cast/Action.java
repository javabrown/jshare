package com.jbrown.cast;

public enum Action {
    DOUBLE_CLICK("Dc", "doubleClick"), RIGHT_CLICK("Rc", "rightClick"), LEFT_CLICK(
            "Lc", "click"), KEY_PRESSED("Kp", "keyPress");

    private final String _actionType; 
    private final String _methodName;

    Action(String actionType, String methodName) {
        _actionType = actionType;
        _methodName = methodName;
    }

    public String getActionName() {
        return _actionType;
    }

    public String getMethodName() {
        return _methodName;
    }

    public static Action getInstance(String actionName) throws Exception {
        for (Action type : Action.values()) {
            if (type.getActionName().equalsIgnoreCase(actionName)) {
                return type;
            }
        }

        throw new Exception("Unknown actionName type:" + actionName);
    }

    public boolean typeOf(Action action) {
        try {
            return getInstance(_actionType).equals(action);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // public ActI getActor() {
    // try {
    // Class clazz = this.getActionClass();
    // Constructor<?> constructor = clazz.getDeclaredConstructor();
    // return (ActI) constructor.newInstance();
    // } catch (Exception ex) {
    // System.err.println("No actor to work with remote-listener" + ex);
    // ex.printStackTrace();
    // }
    // return null;
    // }

    public void trigger(BrownActorI actor, BrownSpot spot) {
        java.lang.reflect.Method method;
        try {
            method = actor.getClass().getMethod(_methodName, BrownSpot.class);
            method.invoke(actor, spot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}