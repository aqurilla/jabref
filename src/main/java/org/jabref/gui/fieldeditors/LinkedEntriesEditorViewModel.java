package org.jabref.gui.fieldeditors;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.undo.UndoManager;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;

import org.jabref.gui.autocompleter.SuggestionProvider;
import org.jabref.gui.util.BindingsHelper;
import org.jabref.logic.integrity.FieldCheckers;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.EntryLinkList;
import org.jabref.model.entry.ParsedEntryLink;
import org.jabref.model.entry.field.Field;

public class LinkedEntriesEditorViewModel extends AbstractEditorViewModel {

    private final BibDatabaseContext databaseContext;
    private final SuggestionProvider<?> suggestionProvider;
    private final ListProperty<ParsedEntryLink> linkedEntries;

    public LinkedEntriesEditorViewModel(Field field, SuggestionProvider<?> suggestionProvider, BibDatabaseContext databaseContext, FieldCheckers fieldCheckers, UndoManager undoManager) {
        super(field, suggestionProvider, fieldCheckers, undoManager);

        this.databaseContext = databaseContext;
        this.suggestionProvider = suggestionProvider;

        linkedEntries = new SimpleListProperty<>(FXCollections.observableArrayList());
        BindingsHelper.bindContentBidirectional(
                linkedEntries,
                text,
                EntryLinkList::serialize,
                newText -> EntryLinkList.parse(newText, databaseContext.getDatabase()));
    }

    public ListProperty<ParsedEntryLink> linkedEntriesProperty() {
        return linkedEntries;
    }

    public StringConverter<ParsedEntryLink> getStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(ParsedEntryLink linkedEntry) {
                if (linkedEntry == null) {
                    return "";
                }
                return linkedEntry.getKey();
            }

            @Override
            public ParsedEntryLink fromString(String key) {
                return new ParsedEntryLink(key, databaseContext.getDatabase());
            }
        };
    }

    public List<ParsedEntryLink> getSuggestions(String request) {
        List<ParsedEntryLink> suggestions = suggestionProvider
                .getPossibleSuggestions()
                .stream()
                .map(suggestion -> suggestion instanceof BibEntry bibEntry ? bibEntry.getCitationKey().orElse("") : (String) suggestion)
                .filter(suggestion -> suggestion.toLowerCase(Locale.ROOT).contains(request.toLowerCase(Locale.ROOT)))
                .map(suggestion -> new ParsedEntryLink(suggestion, databaseContext.getDatabase()))
                .distinct()
                .collect(Collectors.toList());

        ParsedEntryLink requestedLink = new ParsedEntryLink(request, databaseContext.getDatabase());
        if (!suggestions.contains(requestedLink)) {
            suggestions.addFirst(requestedLink);
        }

        return suggestions;
    }

    public void jumpToEntry(ParsedEntryLink parsedEntryLink) {
        // TODO: Implement jump to entry - The implementation can be based on <a href="org.jabref.gui.JabRefFrame.jumpToEntry">JabRefFrame.jumpToEntry</a>
        // TODO: Add toolitp for tag: Localization.lang("Jump to entry")
        // This feature was removed while converting the linked entries editor to JavaFX
        // Right now there is no nice way to re-implement it as we have no good interface to control the focus of the main table
        // (except directly using the JabRefFrame class as below)
        // parsedEntryLink.getLinkedEntry().ifPresent(
        //        e -> frame.getCurrentBasePanel().highlightEntry(e)
        // );
    }
}
