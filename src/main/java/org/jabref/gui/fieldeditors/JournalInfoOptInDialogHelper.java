package org.jabref.gui.fieldeditors;

import org.jabref.gui.DialogService;
import org.jabref.logic.journals.JournalInformationPreferences;
import org.jabref.logic.l10n.Localization;
import org.jabref.logic.util.EnablementStatus;

public class JournalInfoOptInDialogHelper {

    /**
     * Using the journal information data fetcher service needs to be opt-in for GDPR compliance.
     */
    public static boolean isJournalInfoEnabled(DialogService dialogService, JournalInformationPreferences preferences) {
        if (preferences.getEnablementStatus() == EnablementStatus.ENABLED) {
            return true;
        }

        if (preferences.getEnablementStatus() == EnablementStatus.DISABLED) {
            dialogService.notify(
                    Localization.lang("Please enable journal information fetching in %0 > %1",
                            Localization.lang("Preferences"),
                            Localization.lang("Web search"))
            );
            return false;
        }

        boolean journalInfoEnabled = dialogService.showConfirmationDialogAndWait(
                Localization.lang("Remote services"),
                Localization.lang("Allow sending ISSN to a JabRef online service (SCimago) for fetching journal information"));

        preferences.setEnablementStatus(journalInfoEnabled ? EnablementStatus.ENABLED : EnablementStatus.DISABLED);
        return journalInfoEnabled;
    }
}
