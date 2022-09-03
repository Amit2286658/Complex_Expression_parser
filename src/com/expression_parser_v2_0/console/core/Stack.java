package com.expression_parser_v2_0.console.core;

//LIFO Structure
final class Stack<T> {
    private Object[] items;
    //internal Stack pointer;
    private int pointer = -1;

    //last pointer state
    int last_point = 0;

    //for iteration but no actual data modification.
    private int pseudo_counter = -1;

    //for restricting access to certain com.expression_parser_v2_0.console.library.functions depending on the nature of the stack.
    private boolean restriction = false;

    Stack(int size) {
        items = new Object[size];
    }

    Stack(){
        this(10);
    }

    void push(T item){
        //increment the pointer
        pointer++;

        if (pointer <= items.length - 1) {
            items[pointer] = item;
            return;
        }
        //no index is free, allocate a new array with increased size;
        var items1 = new Object[items.length + 10];
        //copy the previous array over to the new one.
        System.arraycopy(items, 0, items1, 0, items.length);
        //reassignment
        items = items1;

        items[pointer] = item;
    }

    void resetToCount(){
        pointer += (last_point - pointer);
        last_point = 0;
    }

    void keepCount(){
        last_point = pointer;
    }

    @SuppressWarnings("unchecked")
    T pop(){
        if (pointer < 0)
            throw new IndexOutOfBoundsException("pointer is out of bounds");

        T item;
        item = (T) items[pointer];
        //no need to remove the data at the current pointer
        //since the pointer will be decremented, and the next time the push function will be called,
        //the pointer will be incremented and any data at the current pointer location will be replaced.

        //decrement the pointer
        pointer--;

        return item;
    }

    T peek(){
        if (pointer >= 0)
            //noinspection unchecked
            return (T) items[pointer];
        throw new IndexOutOfBoundsException("pointer is out of bounds");
    }

    boolean hasNext(){
        //pointer is only incremented when an item is pushed onto the stack, therefore providing
        //null safety by default, meaning wherever the pointer is located, it's guaranteed to be occupied,
        //by a certain object.
        return pointer >= 0;
    }

    //no pointer modification.
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean contains(T item){
        for(Object op : items){
            if (op != null && op.equals(item)){
                return true;
            }
        }
        return false;
    }

    boolean isEmpty(){
        return pointer == -1;
    }

    int getLength(){
        return pointer + 1;
    }

    int getRawLength(){
        return items.length;
    }

    int getFreeLength(){
        return items.length - (pointer + 1);
    }

    int getPointerLocation() {
        return pointer;
    }

    Stack<T> newClone(){
        Stack<T> stack = new Stack<>(getLength());
        for(int i = 0; i < getLength(); i++){
            //noinspection unchecked
            stack.push((T)items[i]);
        }
        return stack;
    }

    Stack<T> reverse() {
        Stack<T> temp = new Stack<>();
        while(hasNext()){
            temp.push(pop());
        }
        return temp;
    }

    //iteration purpose com.expression_parser_v2_0.console.library.functions.
    boolean loop(){
        if (!restriction)
            throw new RuntimeException("com.expression_parser_v2_0.console.library.operations not valid for iterate unrestricted stacks");
        return pseudo_counter >= 0;
    }

    @SuppressWarnings("unchecked")
    T get(){
        if (!restriction)
            throw new RuntimeException("operation not valid for iterate unrestricted stacks");
        T item = (T) items[pseudo_counter];
        pseudo_counter--;
        return item;
    }

    //call it only after having a call made to the "get" function, which is the most natural approach,
    //doing so otherwise will lead to unexpected results, not necessarily an exception,
    //but a completely messed up stack.
    void replace(T item){
        if (!restriction)
            throw new RuntimeException("operation not valid for iterate unrestricted stacks");
        items[pseudo_counter + 1] = item;
    }

    //A one way operation. once set the stacks' nature cannot be changed.
    void iterateRestrict(){
        restriction = true;
    }

    void reset(){
        if (!restriction)
            throw new RuntimeException("operation not valid for iterate unrestricted stacks");
        pseudo_counter = pointer;
    }
}
