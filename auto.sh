FILES=./maps/*

nummaps=(./maps/*)
nummaps=${#nummaps[@]}  
mapnum=1;

aWins=0
bWins=0

teamA=Microscopia
teamB=MicromaniaIsBad

teamAC='\e[1;31m' # Team A's color
teamBC='\e[1;34m' # Team B's color
NC='\e[0m' # No Color

sed -i 's/bc.game.team-a=.*/bc.game.team-a='"$teamA"'/g' bc.conf
sed -i 's/bc.game.team-b=.*/bc.game.team-b='"$teamB"'/g' bc.conf
sed -i 's/bc.server.save-file=.*/bc.server.save-file=.\/matches\/'"$teamA"'-vs-'"$teamB"'\/match.rms/g' bc.conf

echo -e "--------------------------------------------------"

for file in $FILES
do
	mapFileName="${file##*/}"
	mapName="${mapFileName%.*}"
	sed -i 's/bc.game.maps=.*/bc.game.maps='"$mapName"'/g' bc.conf

	printf "Map: %s (%d / %d)\n" $mapName $mapnum $nummaps

	read winner x reason <<< $(ant file | awk '/wins/ {printf "%s\n %s\n", $3, $7} /winning/ {print substr($0, index($0,$7))}')
	
	round=${x%?}

	if [ $winner == $teamA ] 
	then 
		loser=$teamB
		((aWins++))
		color=$teamAC
	else
		loser=$teamA
		((bWins++))
		color=$teamBC
	fi

	echo -e "Winner: ${color}" $winner "${NC}"
	echo -e "Round: " $round
	echo -e "Reason: " $reason

	rm -f './matches/'"$teamA"'-vs-'"$teamB"'/'"$mapName"'*'

	mv './matches/'"$teamA"'-vs-'"$teamB"'/match.rms' './matches/'"$teamA"'-vs-'"$teamB"'/'"$mapName"'-W-'"$winner"'-L-'"$loser"'.rms' 

	echo -e "\n${teamAC}"$teamA "${NC} has " $aWins " wins."
	echo -e "${teamBC}"$teamB "${NC} has " $bWins " wins."
	echo -e "--------------------------------------------------"

	((mapnum++))
done

zdump EST >> './matches/'"$teamA"'-vs-'"$teamB"'/results.txt'
echo $teamA " won " $aWins " times. " $(($((100 * $aWins))/$(($aWins+$bWins)))) "%." >> './matches/'"$teamA"'-vs-'"$teamB"'/results.txt'
echo $teamB " won " $bWins " times. " $(($((100 * $bWins))/$(($aWins+$bWins)))) "%." >> './matches/'"$teamA"'-vs-'"$teamB"'/results.txt'
