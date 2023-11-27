# https://discordpy.readthedocs.io/en/stable/

import os

import discord
from discord.ext import commands
import coghandler
from dotenv import load_dotenv

###################################
# Global vars and objects
###################################
load_dotenv()

intents = discord.Intents.default()
intents.message_content = True

client = commands.Bot(command_prefix='!', intents=intents)


@client.event
async def on_ready():
  print('Logged in as {0.user}'.format(client))
  await client.add_cog(coghandler.AccountabilityCog(client))


@client.event
async def on_message(message):
  #on_message triggers on the bot's own messages
  if message.author == client.user:
    return
  #Stops on_message from preventing @client.command() being executed
  await client.process_commands(message)


###################################
# Commands
###################################
@client.command()
async def hello(ctx):
  await ctx.send('Hello!')


###################################
# Connection Service
###################################
try:
  token = os.getenv("DISCORD_TOKEN") or ""
  client.run(token)
except discord.HTTPException as e:
  if e.status == 429:
    print("Discord servers denied the connection for making too many requests")
  else:
    raise e
