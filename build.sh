#!/bin/bash

if [[ -z "${MCP}" ]]; then
	echo "Please set \$MCP to your MCP working dir."
	exit
fi
if [[ -z "${PROJBASE}" ]]; then
	echo "Assuming project basedir is here."
	PROJBASE=`pwd`
fi

BUILDSRC=$MCP/src
CLEANSRC=$MCP/src-dev

SPRITEFOLDER=PowerConverterSprites
MODNAME=PowerConverters
MODVERSION=1.3.2
ZIPPATH=`which zip`

SRCBASE=$PROJBASE/source
CLIENTSRC=$SRCBASE/client
SERVERSRC=$SRCBASE/server
COMMONSRC=$SRCBASE/common

RELEASEBASE=$PROJBASE/release
RELEASECLIENT=$RELEASEBASE/clienttemp
RELEASESERVER=$RELEASEBASE/servertemp

echo "This assumes you have a clean (with BC source and IC2 API) copy of the decompiled code in src-dev. press ctrl-c now if you don't."
echo "Press any key to continue."

read

mkdir -p $RELEASECLIENT
mkdir -p $RELEASESERVER

rm -rf $BUILDSRC
cp -a $CLEANSRC $BUILDSRC
cp -a $COMMONSRC $BUILDSRC/minecraft/
cp -a $COMMONSRC $BUILDSRC/minecraft_server
cp -a $CLIENTSRC $BUILDSRC/minecraft/
cp -a $SERVERSRC $BUILDSRC/minecraft_server/

pushd $MCP
./recompile.sh
./reobfuscate.sh

rm -rf $RELEASEBASE/*.zip
rm -rf $RELEASECLIENT/*
rm -rf $RELEASESERVER/*
cp -a $MCP/reobf/minecraft/* $RELEASECLIENT/
cp -a $MCP/reobf/minecraft_server/* $RELEASESERVER/
cp -a $PROJBASE/sprites $RELEASECLIENT/$SPRITEFOLDER
rm -rf $RELEASECLIENT/buildcraft $RELEASECLIENT/net
rm -rf $RELEASECLIENT/ic2 $RELEASECLIENT/BuildCraft* $RELEASECLIENT/mod_BuildCraft* $RELEASECLIENT/mod_IC2*
rm -rf $RELEASESERVER/buildcraft $RELEASESERVER/net
rm -rf $RELEASESERVER/ic2 $RELEASESERVER/BuildCraft* $RELEASESERVER/mod_BuildCraft* $RELEASESERVER/mod_IC2*


cd $RELEASECLIENT
$ZIPPATH -r -q $RELEASEBASE/"$MODNAME"_Client_"$MODVERSION".zip *

cd $RELEASESERVER
$ZIPPATH -r -q $RELEASEBASE/"$MODNAME"_Server_"$MODVERSION".zip *

popd
