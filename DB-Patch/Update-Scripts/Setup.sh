export str1= 
export str2=
export str3=
export str4=
export str= 

for i in $(cat Parameters.dat)
do
	if [ "${i:0:8}" = "DATABASE" ]
	then
		echo $i
		export str1="${i:9}"
	fi
	if [ "${i:0:8}" = "PASSWORD" ]
	then
		echo $i
		export str2="${i:9}"
	fi
	if [ "${i:0:8}" = "USERNAME" ]
	then
		echo $i
		export str3="${i:9}"
	fi
	if [ "${i:0:8}" = "BRK_CODE" ]
	then
		echo $i
		export str4="${i:9}"
	fi
done
bash execute.sh $str1 $str2 $str3 $str4
