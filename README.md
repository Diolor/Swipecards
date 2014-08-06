Swipecards
==========

A Tinder-like cards effect as of August 2014. You can swipe left or right to like or dislike the content.


---

The library creates a similar effect to Tinder's swipable cards with Fling animation.

It was inspired by kikoso's https://github.com/kikoso/Swipeable-Cards but I decided to create a more simple and fresh approach.

Example
=======

The example is quite straightforward and you may find it under the relevant directory.

```java

//implement the onFlingListener
public class MyActivity extends Activity implements onFlingListener {
    ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...

        //add the view via xml or programmatically
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        al = new ArrayList<String>();
        al.add("php");
        al.add("c");
        al.add("python");
        al.add("java");

        //choose your favorite adapter
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item, R.id.helloText, al );
        
        //set the listener and the adapter
        flingContainer.setRemoveObjectsListener(this);
        flingContainer.setAdapter(arrayAdapter);
    }

    @Override
    public void removeFirstObjectInAdapter() {
        // this is the simplest way to delete an object from the Adapter (/AdapterView)
        Log.d("LIST", "removed object!");
        al.remove(0);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLeftCardExit() {
        //Do something on the left!
        Toast.makeText(this,"Left!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRightCardExit() {
        Toast.makeText(this,"Right!",Toast.LENGTH_SHORT).show();
    }


}
```
