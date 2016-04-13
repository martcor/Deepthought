#  Deepthought 

Telegram Client written in Java to support multiple custom implementations

## Contributions

Feel free to fork this project, work on it and then make a pull request to **dev branch**. 
Most of the times I will accept them if they add something valuable to the code. 

### Contributing to Main Code

Go ahead an make a pull request to dev branch with changes in *api*, *bot*, *mtproto* or *tl*.

### Adding new plugins

Make a pull request with the new plugin. Please follow these rules:

  1. Add the plugin under *plugins* package in its own package.
  2. Don't make a plugin dependent on a different one.
  3. A nice description of the plugin is welcome inside the package in a file called **Plugin.md**
    

Please, **DO NOT PUSH ANY API KEY OR API HASH**, I will never accept a pull request with that content.


## Usage

Current *Main.java* has a example of usage with echo bot, to run it you need a couple of steps first
    
  1. In *BotConstants.java*, set up your own API_KEY and API_HASH. You can get them [here](https://core.telegram.org/api/obtaining_api_id)
  2. In *DatabaseConstants.java*, set up your database configuration (database name, user, password). Currently the code support mysql databases.
  3. In *Main.java* write your name in the *BotConfig.java* instance.

In any case, this is the things that needs to be done before using it:

  1. In *BotConstants.java*, set up your own API_KEY and API_HASH. You can get them [here](https://core.telegram.org/api/obtaining_api_id)
  2. In the plugin you want to build, create your own class that extends *UpdatesHandlerBase.java* (or *DefaultUpdatesHandler.java* if you only need a simpler version). This will be in charge of handling every update received.
  3. In the plugin you want to build, create your own class that implements *ChatUpdatesBuilder.java*. This builder must be the one in charge of creating the unique instance of your previous handler.
  4. For building points 2 and 3, you are gonna need a DatabaseManager implementation (this doesn't need to have a real database behind it, but it must be able to store *chats*, *users* and *differences data* somewhere so the client works correctly.
  5. In *Main.java* build your own BotConfig with your phone number (remember that it must be in international format +1XXXXXXXXX, but without the *+* symbol) and the name of the auth file where the session must be stored. Then build your own ChatUpdatesBuilder implementation and finally create your own *TelegramBot.java* and call its methods *init* and *startBot*.
  6. It should start working.
    
    
## Example bots

Create your own stickers with [Clippy](https://telegram.me/clippy)
    
    
## Telegram API and MTPROTO:

If you want more information about Telegram API, you can go [here](https://core.telegram.org/api#telegram-api). And [here](https://core.telegram.org/mtproto) you will find extra information about mtproto.
 
## Questions or Suggestions
Feel free to create issues [here](https://github.com/rubenlagus/Deepthought/issues) as you need
 
## Disclaimer

I know this project still have many things to improve, please be patient or help me with it.
 
## Acknowledgments

  * To ex3ndr, whose code was the base of this bot long, long ago. Not much remains from that point, but still there are a few files.
  * To [Drklo](https://github.com/drklo), whose code was also helpful in this project.
  * To [Marvin](https://github.com/dapoldi) for all the help, testing and programing that he spends in this project.
  * To [Telegram Team](https://telegram.org) for their help while developing it.
 
## License 

Find a copy [here](https://github.com/rubenlagus/Deepthought/blob/master/LICENSE) 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
