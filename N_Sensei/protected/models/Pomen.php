<?php

/**
 * This is the model class for table "pomen".
 *
 * The followings are the available columns in table 'pomen':
 * @property integer $ID
 * @property integer $SENZOR_ID
 * @property string $POMEN
 * @property string $MIN_VREDNOST
 * @property string $MAX_VREDNOST
 *
 * The followings are the available model relations:
 * @property Senzor $sENZOR
 */
class Pomen extends CActiveRecord
{
	/**
	 * @return string the associated database table name
	 */
	public function tableName()
	{
		return 'pomen';
	}

	/**
	 * @return array validation rules for model attributes.
	 */
	public function rules()
	{
		// NOTE: you should only define rules for those attributes that
		// will receive user inputs.
		return array(
			array('SENZOR_ID, POMEN, MIN_VREDNOST, MAX_VREDNOST', 'required'),
			array('SENZOR_ID', 'numerical', 'integerOnly'=>true),
			array('POMEN', 'length', 'max'=>200),
			array('MIN_VREDNOST, MAX_VREDNOST', 'length', 'max'=>100),
			// The following rule is used by search().
			// @todo Please remove those attributes that should not be searched.
			array('ID, SENZOR_ID, POMEN, MIN_VREDNOST, MAX_VREDNOST', 'safe', 'on'=>'search'),
		);
	}

	/**
	 * @return array relational rules.
	 */
	public function relations()
	{
		// NOTE: you may need to adjust the relation name and the related
		// class name for the relations automatically generated below.
		return array(
			'sENZOR' => array(self::BELONGS_TO, 'Senzor', 'SENZOR_ID'),
		);
	}

	/**
	 * @return array customized attribute labels (name=>label)
	 */
	public function attributeLabels()
	{
		return array(
			'ID' => 'ID',
			'SENZOR_ID' => 'Senzor',
			'POMEN' => 'Pomen',
			'MIN_VREDNOST' => 'Min Vrednost',
			'MAX_VREDNOST' => 'Max Vrednost',
		);
	}

	/**
	 * Retrieves a list of models based on the current search/filter conditions.
	 *
	 * Typical usecase:
	 * - Initialize the model fields with values from filter form.
	 * - Execute this method to get CActiveDataProvider instance which will filter
	 * models according to data in model fields.
	 * - Pass data provider to CGridView, CListView or any similar widget.
	 *
	 * @return CActiveDataProvider the data provider that can return the models
	 * based on the search/filter conditions.
	 */
	public function search()
	{
		// @todo Please modify the following code to remove attributes that should not be searched.

		$criteria=new CDbCriteria;

		$criteria->compare('ID',$this->ID);
		$criteria->compare('SENZOR_ID',$this->SENZOR_ID);
		$criteria->compare('POMEN',$this->POMEN,true);
		$criteria->compare('MIN_VREDNOST',$this->MIN_VREDNOST,true);
		$criteria->compare('MAX_VREDNOST',$this->MAX_VREDNOST,true);

		return new CActiveDataProvider($this, array(
			'criteria'=>$criteria,
		));
	}

	/**
	 * Returns the static model of the specified AR class.
	 * Please note that you should have this exact method in all your CActiveRecord descendants!
	 * @param string $className active record class name.
	 * @return Pomen the static model class
	 */
	public static function model($className=__CLASS__)
	{
		return parent::model($className);
	}
}
