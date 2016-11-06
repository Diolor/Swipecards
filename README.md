Swipecards
==========

Travis master: [![Build Status](https://travis-ci.org/Diolor/Swipecards.svg?branch=master)](https://travis-ci.org/Diolor/Swipecards) 


A Tinder-like cards effect as of August 2014. You can swipe left or right to like or dislike the content.
The library creates a similar effect to Tinder's swipable cards with Fling animation.

It handles greatly asynchronous loading of adapter's data and uses the same layout parameters as FrameLayout (you may use `android:layout_gravity` in your layout xml file).

![ ](/screenshot.gif)

---


Installation
=======

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.lorentzos.swipecards/library/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.lorentzos.swipecards/library)

Go ahead find the latest version on [Gradle please] cowboy! 
Be sure to add the `@aar` suffix.


```groovy
dependencies {
    compile 'com.lorentzos.swipecards:library:X.X.X@aar'
}
```



Example
=======

The example is quite straightforward and documented in comments. 
You may find it under the relevant directory.


```java

//implement the onFlingListener
public class MyActivity extends Activity {
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
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(MyActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(MyActivity.this, "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }
        });
        
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeToast(MyActivity.this, "Clicked!");
            }
        });
    }
}
```

You can alternatively use a helpful method which sets in one line both the listeners and the adapter.

```java
    // where "this" stands for the Context
    flingContainer.init(this, arrayAdapter);
````


**Adding buttons** is easy. Get the top card listener and trigger manually the right or left animation.
On the end of the animation the above listeners (e.g. removeFirstObjectInAdapter) will be triggered depending on the direction.
 

```java
    /**
     * Trigger the right event manually.
     */
    flingContainer.getTopCardListener().selectRight();
```



**Tip**: If you start a new Activity in the `onItemClicked` you will probably want to avoid double activity instances.
If so these solutions might work for you: [1](http://stackoverflow.com/a/8077776/1447885), 
[2](http://stackoverflow.com/a/17270364/1447885) and I personally prefer [3](http://stackoverflow.com/a/21906867/1447885)



Configuration
=============

You can optionally specify some attrs for the animation and the stack. The easiest way is in xml:

```xml
<com.lorentzos.flingswipe.SwipeFlingAdapterView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:rotation_degrees="16"
    app:max_visible="4"
    app:min_adapter_stack="6" />
```

Or use styles:

```xml
<!-- Base application theme. -->
<style name="AppTheme" parent="android:Theme.Holo.Light.DarkActionBar">
    <!-- Customize your theme here. -->
    <item name="SwipeFlingStyle">@style/SwipeFling</item>
</style>
```

- rotation_degrees: the degrees of the card rotation offset
- max_visible: the max visible cards at the time
- min_adapter_stack: the min number of objects left. Initiates onAdapterAboutToEmpty() method.

License
======

```
   Copyright 2014 Dionysis Lorentzos

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

[Gradle please]:http://gradleplease.appspot.com/#com.lorentzos.swipecards
[Kikoso's Swipeable-Cards]:https://github.com/kikoso/Swipeable-Cards

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Swipecards-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1028)
