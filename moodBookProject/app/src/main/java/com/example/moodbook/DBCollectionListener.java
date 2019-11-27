package com.example.moodbook;

/**
 * Implemented by PageFragment to get real-time updated list from db
 */
public interface DBCollectionListener {
    /**
     * This defines task to be done before getting all the items
     * e.g. clearing list in PageFragment
     */
    void beforeGettingCollection();

    /**
     * This defines task to be done when getting an item
     * e.g. add item into list in PageFragment
     * @param item
     */
    void onGettingItem(Object item);

    /**
     * This defines task to be done after getting all the items
     * i.e. notify PageFragment all the items are saved into the list
     */
    void afterGettingCollection();
}
