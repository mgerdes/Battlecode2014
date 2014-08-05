FILES=~/Battlecode2014/maps/*

aWins=0
bWins=0

teamA=MarkyMark
teamB=bot

sed -i 's/bc.game.team-a=.*/bc.game.team-a='"$teamA"'/g' bc.conf
sed -i 's/bc.game.team-b=.*/bc.game.team-b='"$teamB"'/g' bc.conf
sed -i 's/bc.server.save-file=.*/bc.server.save-file=.\/matches\/'"$teamA"'-vs-'"$teamB"'\/match.rms/g' bc.conf

for file in $FILES
do
	mapFileName="${file##*/}"
	mapName="${mapFileName%.*}"
	sed -i 's/bc.game.maps=.*/bc.game.maps='"$mapName"'/g' bc.conf

	printf "Map: %s\n" $mapName

	read winner x reason <<< $(ant file | awk '/wins/ {printf "%s\n %s\n", $3, $7} /winning/ {print substr($0, index($0,$7))}')
	
	round=${x%?}

	echo "Winner: " $winner
	echo "Round: " $round
	echo "Reason: " $reason

	if [ $winner == $teamA ] 
	then 
		loser=$teamB
		((aWins++))
	else
		loser=$teamA
		((bWins++))
	fi

	mv './matches/'"$teamA"'-vs-'"$teamB"'/match.rms' './matches/'"$teamA"'-vs-'"$teamB"'/'"$mapName"'-W-'"$winner"'-L-'"$loser"'.rms' 

	echo "--------------------------------------------------"
	echo $teamA " has " $aWins " wins."
	echo $teamB " has " $bWins " wins."
	echo "--------------------------------------------------"
done

zdump EST >> './matches/'"$teamA"'-vs-'"$teamB"'/results.txt'
echo $teamA " won " $aWins " times. " $(($((100 * $aWins))/$(($aWins+$bWins)))) "%." >> './matches/'"$teamA"'-vs-'"$teamB"'/results.txt'
echo $teamB " won " $bWins " times. " $(($((100 * $bWins))/$(($aWins+$bWins)))) "%." >> './matches/'"$teamA"'-vs-'"$teamB"'/results.txt'
