package configutil

import (
	"bytes"
	"os"

	"github.com/spf13/viper"
)

func LoadViper(configPath string, defaultConfig []byte, options ...option) error {
	viperCfg := viper.GetViper()
	for _, option := range options {
		option.Apply(viperCfg)
	}

	if configPath != "" {
		_, err := os.Stat(configPath)
		if err == nil {
			viperCfg.SetConfigFile(configPath)
		}
	}

	if err := viperCfg.ReadInConfig(); err == nil {
		return nil
	}

	if defaultConfig != nil {
		if err := viperCfg.ReadConfig(bytes.NewBuffer(defaultConfig)); err != nil {
			return err
		}
	}

	return nil
}
