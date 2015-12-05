package de.espend.idea.laravel.tests.controller;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.laravel.tests.LaravelLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see de.espend.idea.laravel.controller.ControllerReferences
 */
public class ControllerReferencesTest extends LaravelLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject("routing.php"));
    }

    protected String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testRouteParameter() {
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
            "Route::get(null, '<caret>');\n",
            "FooController@foo", "Foo\\Controllers\\BarController@foo"
        );

        assertNavigationMatch(PhpFileType.INSTANCE, "<?php\n" +
                "Route::get(null, 'FooController@foo<caret>');\n",
            PlatformPatterns.psiElement(Method.class)
        );

        assertNavigationMatch(PhpFileType.INSTANCE, "<?php\n" +
                "Route::get(null, 'Foo\\Controllers\\BarController@foo<caret>');\n",
            PlatformPatterns.psiElement(Method.class)
        );
    }

    public void testRouteUsesInsideArray() {
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "Route::get('/', [\n" +
                "  'uses' => '<caret>', \n" +
                "]);",
            "FooController@foo", "Foo\\Controllers\\BarController@foo"
        );
    }

    public void testRouteGroups() {
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "Route::group(['namespace' => 'Foo\\Controllers'], function() {\n" +
                "    Route::get('/', '<caret>');\n" +
                "});",
            "BarController@foo"
        );

        assertNavigationMatch(PhpFileType.INSTANCE, "<?php\n" +
                "Route::group(['namespace' => 'Foo\\Controllers'], function() {\n" +
                "    Route::get('/', 'BarController@foo<caret>');\n" +
                "});",
            PlatformPatterns.psiElement(Method.class).withParent(
                PlatformPatterns.psiElement(PhpClass.class).withName("BarController")
            )
        );
    }
}
