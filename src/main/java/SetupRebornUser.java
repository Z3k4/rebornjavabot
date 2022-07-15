import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class SetupRebornUser implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof MessageReceivedEvent) {
            String message =  ((MessageReceivedEvent) event).getMessage().getContentRaw();
            if(message.indexOf("!rset_steam") >= 0) {
            }
        }
    }
}
