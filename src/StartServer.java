import controller.Server;

/**
 * Starts the server code on a default port unless a command line argument is
 * given in which case the server will be started on the argument's port number.
 * 
 * @author William Snider
 * 
 */
public class StartServer {
    public static void main(String[] args) {
	int port = 27000;

	if (args.length > 0) {
	    port = Integer.parseInt(args[0]);
	}

	new Server(port);
    }
}