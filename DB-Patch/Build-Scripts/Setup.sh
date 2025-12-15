export str1= 
export str2=
export str= 


for i in $(cat Parameters.dat)
do
	if [ "${i:0:7}" = "CON_STR" ]
	then
		echo $i
		export str1="${i:8}"
	fi
	if [ "${i:0:7}" = "SYS_PWD" ]
	then
		echo $i
		export str2="${i:8}"
	fi
done
bash execute.sh $str1 $str2
