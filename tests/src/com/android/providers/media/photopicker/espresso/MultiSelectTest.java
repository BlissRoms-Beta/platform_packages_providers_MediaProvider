/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.providers.media.photopicker.espresso;

import static androidx.test.InstrumentationRegistry.getTargetContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.android.providers.media.photopicker.espresso.RecyclerViewTestUtils.assertItemDisplayed;
import static com.android.providers.media.photopicker.espresso.RecyclerViewTestUtils.assertItemSelected;
import static com.android.providers.media.photopicker.espresso.RecyclerViewTestUtils.assertItemNotSelected;
import static com.android.providers.media.photopicker.espresso.RecyclerViewTestUtils.clickItem;

import static org.hamcrest.Matchers.not;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.android.providers.media.R;

import org.junit.runner.RunWith;
import org.junit.Rule;
import org.junit.Test;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MultiSelectTest extends PhotoPickerBaseTest {
    private static final int ICON_THUMBNAIL_ID = R.id.icon_thumbnail;
    private static final int ICON_CHECK_ID = R.id.icon_check;

    @Rule
    public ActivityScenarioRule<PhotoPickerTestActivity> mRule
            = new ActivityScenarioRule<>(PhotoPickerBaseTest.getMultiSelectionIntent());

    @Test
    public void testMultiselect_selectIcon() {
        onView(withId(PICKER_TAB_RECYCLERVIEW_ID)).check(matches(isDisplayed()));

        // position=1 is the first image item
        final int position = 1;
        // Check select icon is visible
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, R.id.overlay_gradient);
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_CHECK_ID);

        // Verify that select icon is not selected yet
        assertItemNotSelected(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_CHECK_ID);

        // Select 1st item thumbnail and verify select icon is selected
        clickItem(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_THUMBNAIL_ID);
        assertItemSelected(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_CHECK_ID);

        // Deselect the item to check item is marked as not selected.
        clickItem(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_THUMBNAIL_ID);
        assertItemNotSelected(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_CHECK_ID);

        // Now, click on the select/check icon, verify we can also click on check icon to select or
        // deselect an item.
        clickItem(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_CHECK_ID);
        assertItemSelected(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_CHECK_ID);

        // Click on recyclerView item, this deselects the item. Verify that we can click on any
        // region on the recyclerView item to select/deselect the item.
        clickItem(PICKER_TAB_RECYCLERVIEW_ID, position, /* targetViewId */ -1);
        assertItemNotSelected(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_CHECK_ID);
    }

    @Test
    public void testMultiSelect_bottomBar() {
        onView(withId(PICKER_TAB_RECYCLERVIEW_ID)).check(matches(isDisplayed()));

        final int bottomBarId = R.id.picker_bottom_bar;
        final int viewSelectedId = R.id.button_view_selected;
        final int addButtonId = R.id.button_add;

        // Initially, buttons should be hidden
        onView(withId(bottomBarId)).check(matches(not(isDisplayed())));

        // position=1 is the first image item
        final int position = 1;
        // Selecting one item shows view selected and add button
        clickItem(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_THUMBNAIL_ID);

        onView(withId(bottomBarId)).check(matches(isDisplayed()));
        onView(withId(viewSelectedId)).check(matches(isDisplayed()));
        onView(withId(viewSelectedId)).check(matches(withText(R.string.picker_view_selected)));
        onView(withId(addButtonId)).check(matches(isDisplayed()));

        // When the selected item count is 0, ViewSelected and add button should hide
        clickItem(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_THUMBNAIL_ID);
        onView(withId(bottomBarId)).check(matches(not(isDisplayed())));
        onView(withId(viewSelectedId)).check(matches(not(isDisplayed())));
        onView(withId(addButtonId)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testMultiSelect_addButtonText() {
        onView(withId(PICKER_TAB_RECYCLERVIEW_ID)).check(matches(isDisplayed()));

        final int addButtonId = R.id.button_add;
        final String addButtonString =
                getTargetContext().getResources().getString(R.string.add);

        // Selecting one item will enable add button and show "Add (1)" as button text
        clickItem(PICKER_TAB_RECYCLERVIEW_ID, /* position */ 1, ICON_THUMBNAIL_ID);

        onView(withId(addButtonId)).check(matches(isDisplayed()));
        onView(withId(addButtonId)).check(matches(withText(addButtonString + " (1)")));

        // When the selected item count is 2, "Add (2)" should be displayed
        clickItem(PICKER_TAB_RECYCLERVIEW_ID, /* position */ 2, ICON_THUMBNAIL_ID);
        onView(withId(addButtonId)).check(matches(isDisplayed()));
        onView(withId(addButtonId)).check(matches(withText(addButtonString + " (2)")));

        // When the item is deselected add button resets to selected count
        clickItem(PICKER_TAB_RECYCLERVIEW_ID, /* position */ 2, ICON_THUMBNAIL_ID);
        onView(withId(addButtonId)).check(matches(isDisplayed()));
        onView(withId(addButtonId)).check(matches(withText(addButtonString + " (1)")));
    }
}